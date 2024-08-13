package GInternational.server.api.utilities;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class AmazonBigDecimalSerializer extends JsonSerializer<BigDecimal> {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0");

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            String formattedValue = DECIMAL_FORMAT.format(value);
            gen.writeString(formattedValue);
        }
    }
}