package com.digirati.taxman.rest.server.infrastructure.event;

import com.digirati.taxman.analysis.index.TermIndex;
import com.digirati.taxman.common.taxonomy.ConceptLabelExtractor;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.util.MessageBatch;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Listener to respond to changes in the state of a concept.
 */
@ApplicationScoped
public class ConceptEventListener extends ReceiverAdapter {
    @Inject
    JChannel channel;

    @ConfigProperty(name = "taxman.analysis.default-lang.key", defaultValue = "en")
    String defaultLanguageKey;

    @Inject
    TermIndex<String, UUID> index;

    protected void init(@Observes StartupEvent evt) throws Exception {
        channel.setReceiver(this);
    }

    @Override
    public void receive(Message msg) {
        handle(msg.getObject());
    }

    public void receive(MessageBatch batch) {
        for (Message msg : batch) {
            handle(msg.getObject(ConceptChangeEvent.class.getClassLoader()));
        }
    }

    public void notify(ConceptEvent event)  {
        ConceptModel previous = event.getPrevious();
        List<String> removed = new ArrayList<>();
        UUID uuid = null;

        if (previous != null) {
            uuid = previous.getUuid();
            consumeLabels(previous, removed::add);
        }

        List<String> added = new ArrayList<>();
        ConceptModel current = event.getConcept();

        if (current != null) {
            uuid = current.getUuid();
            consumeLabels(current, added::add);
        }

        ConceptChangeEvent changeEvent = new ConceptChangeEvent(uuid, current.getProjectId(), added, removed);
        try {
            channel.send(new Message(/* null = all in the cluster */ null, changeEvent));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void handle(ConceptChangeEvent event) {
        var uuid = event.getUuid();
        String projectId = event.getProjectId();

        for (String added : event.getAdded()) {
            index.add(projectId, uuid, added);
        }

        for (String removed : event.getRemoved()) {
            index.remove(uuid, removed);
        }
    }

    private void consumeLabels(ConceptModel concept, Consumer<String> consumer) {
        var labelExtractor = new ConceptLabelExtractor(concept);

        labelExtractor.extractTo((property, literal) -> {
            Collection<String> values = literal.get(defaultLanguageKey);
            values.forEach(consumer::accept);
        });
    }
}
