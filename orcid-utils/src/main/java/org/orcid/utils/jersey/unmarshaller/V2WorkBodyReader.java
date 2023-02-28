package org.orcid.utils.jersey.unmarshaller;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.orcid.jaxb.model.record_v2.Work;

import javax.ws.rs.Consumes;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

@Provider
@Consumes({"application/xml", "application/json"})
public class V2WorkBodyReader implements MessageBodyReader<Work> {

    private final Unmarshaller unmarshaller;
    
    public V2WorkBodyReader() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Work.class);
            unmarshaller = jaxbContext.createUnmarshaller();            
        } catch (JAXBException jaxbException) {
            throw new ProcessingException("Error deserializing a " + Work.class,
                jaxbException);
        }
    }
    
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == Work.class;
    }

    @Override
    public Work readFrom(Class<Work> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
            InputStream entityStream) throws IOException, WebApplicationException {
        try {
            return (Work) unmarshaller.unmarshal(entityStream);
        } catch (JAXBException jaxbException) {
            throw new ProcessingException("Error deserializing a " + Work.class,
                jaxbException);
        }
    }

}
