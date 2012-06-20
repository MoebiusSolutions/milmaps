package mil.usmc.resources;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;

/**
 * Can represent any resource that requires
 * a transform.
 */
public class TransformResource {

    private static final Logger LOGGER = Logger.getLogger(TransformResource.class.getName());
    private Transformer m_transformer;
    private Source m_input;

    public TransformResource(Transformer t, Source i) {
        m_transformer = t;
        m_input = i;
    }

    public Transformer getTransformer() {
        return m_transformer;
    }

    public void setTransformer(Transformer transformer) {
        m_transformer = transformer;
    }

    public Source getInput() {
        return m_input;
    }

    public void setInput(Source input) {
        m_input = input;
    }

    public static InputStream tryGetResourceAsStream(String xsltFile) {
        InputStream is = TransformResource.class.getResourceAsStream(xsltFile);
        if (is == null) {
            LOGGER.log(Level.WARNING, "Failed to find stylesheet");
            throw new RuntimeException("Failed to find stylesheet");
        }
        return is;
    }
}
