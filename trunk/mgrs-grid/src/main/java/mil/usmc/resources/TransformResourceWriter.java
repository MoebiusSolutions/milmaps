package mil.usmc.resources;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import com.moesol.threading.Threading;
import com.moesol.threading.ThreadingModel;

/**
 * Provide a message body writer to write XML by running a transform.
 */
@Provider
@Threading(ThreadingModel.FREE)
public class TransformResourceWriter implements MessageBodyWriter<TransformResource> {

    private static final Logger LOGGER = Logger.getLogger(TransformResourceWriter.class.getName());

    private void checkForNull(String msg, Object object) {
        if (object == null) {
            throw new NullPointerException(msg);
        }
    }

    @Override
    public void writeTo(
            TransformResource transformResource,
            Class<?> cls,
            Type type,
            Annotation[] annotations,
            MediaType mediaType,
            MultivaluedMap<String, Object> map,
            OutputStream out) throws IOException {
        try {
            doWrite(transformResource, out);
        } catch (TransformerException e) {
            LOGGER.log(Level.SEVERE, "Transformer exception");
            throw new IOException(e);
        }
    }

    public void doWrite(TransformResource transformResource, OutputStream out) throws TransformerException {
        Source xmlSource = transformResource.getInput();
        Result outputTarget = new StreamResult(out);
        checkForNull("source", xmlSource);
        checkForNull("result", outputTarget);
        transformResource.getTransformer().transform(xmlSource, outputTarget);
    }

    @Override
    public long getSize(TransformResource arg0, Class<?> arg1, Type arg2, Annotation[] arg3, MediaType arg4) {
        return -1L;
    }

    @Override
    public boolean isWriteable(Class<?> cls, Type arg1, Annotation[] arg2, MediaType arg3) {
        return TransformResource.class.isAssignableFrom(cls);
    }
}
