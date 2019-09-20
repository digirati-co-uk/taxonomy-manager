CREATE OR REPLACE FUNCTION is_plain_literal(val jsonb) RETURNS boolean AS
$$
BEGIN
    RETURN NOT EXISTS(SELECT 1
                      FROM jsonb_each(val) AS j
                      WHERE jsonb_typeof(j.value) NOT IN ('string', 'array'));
END;
$$ LANGUAGE plpgsql;
COMMENT ON FUNCTION is_plain_literal(val jsonb) IS 'Check if a JSONB value contains only an object with string or array values';
