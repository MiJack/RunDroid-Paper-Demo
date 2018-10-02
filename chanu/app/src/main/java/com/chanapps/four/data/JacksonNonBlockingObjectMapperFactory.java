package com.chanapps.four.data;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.deser.*;
import java.io.IOException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 2/25/13
 * Time: 12:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class JacksonNonBlockingObjectMapperFactory {

    public JacksonNonBlockingObjectMapperFactory() {}

    public static class NonBlockingIntegerDeserializer extends JsonDeserializer<Integer> {
        private JsonDeserializer<?> delegate;
        public NonBlockingIntegerDeserializer() {
            this.delegate = new StdDeserializer.IntegerDeserializer(Integer.class, 0);
        }
        @Override
        public Integer deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            com.mijack.Xlog.logMethodEnter("java.lang.Integer com.chanapps.four.data.JacksonNonBlockingObjectMapperFactory$NonBlockingIntegerDeserializer.deserialize(org.codehaus.jackson.JsonParser,DeserializationContext)",this,jp,ctxt);try{Object o = null;
            try {
                if (delegate != null)
                    {o = delegate.deserialize(jp, ctxt);}
            }
            catch (JsonMappingException e) {
                try {
                    boolean b = jp.getBooleanValue();
                    o = b ? 1 : 0;
                }
                catch (Exception e2) {
                    if (delegate != null)
                        {o = delegate.getNullValue();}
                }
            }
            catch (Exception e) {
                if (delegate != null)
                    {o = delegate.getNullValue();}
            }
            {com.mijack.Xlog.logMethodExit("java.lang.Integer com.chanapps.four.data.JacksonNonBlockingObjectMapperFactory$NonBlockingIntegerDeserializer.deserialize(org.codehaus.jackson.JsonParser,DeserializationContext)",this);return (Integer)o;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Integer com.chanapps.four.data.JacksonNonBlockingObjectMapperFactory$NonBlockingIntegerDeserializer.deserialize(org.codehaus.jackson.JsonParser,DeserializationContext)",this,throwable);throw throwable;}
        }
    }

    public static class NonBlockingLongDeserializer extends JsonDeserializer<Long> {
        private JsonDeserializer<?> delegate;
        public NonBlockingLongDeserializer() {
            this.delegate = new StdDeserializer.LongDeserializer(Long.class, 0L);
        }
        @Override
        public Long deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            com.mijack.Xlog.logMethodEnter("java.lang.Long com.chanapps.four.data.JacksonNonBlockingObjectMapperFactory$NonBlockingLongDeserializer.deserialize(org.codehaus.jackson.JsonParser,DeserializationContext)",this,jp,ctxt);try{Object o = null;
            try {
                if (delegate != null)
                    {o = delegate.deserialize(jp, ctxt);}
            }
            catch (Exception e) {
                if (delegate != null)
                    {o = delegate.getNullValue();}
            }
            {com.mijack.Xlog.logMethodExit("java.lang.Long com.chanapps.four.data.JacksonNonBlockingObjectMapperFactory$NonBlockingLongDeserializer.deserialize(org.codehaus.jackson.JsonParser,DeserializationContext)",this);return (Long)o;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Long com.chanapps.four.data.JacksonNonBlockingObjectMapperFactory$NonBlockingLongDeserializer.deserialize(org.codehaus.jackson.JsonParser,DeserializationContext)",this,throwable);throw throwable;}
        }
    }

    public static class NonBlockingBooleanDeserializer extends JsonDeserializer<Boolean> {
        private JsonDeserializer<?> delegate;
        public NonBlockingBooleanDeserializer() {
            this.delegate = new StdDeserializer.BooleanDeserializer(Boolean.class, false);
        }
        @Override
        public Boolean deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            com.mijack.Xlog.logMethodEnter("java.lang.Boolean com.chanapps.four.data.JacksonNonBlockingObjectMapperFactory$NonBlockingBooleanDeserializer.deserialize(org.codehaus.jackson.JsonParser,DeserializationContext)",this,jp,ctxt);try{Object o = null;
            try {
                if (delegate != null)
                    {o = delegate.deserialize(jp, ctxt);}
            }
            catch (Exception e) {
                if (delegate != null)
                    {o = delegate.getNullValue();}
            }
            {com.mijack.Xlog.logMethodExit("java.lang.Boolean com.chanapps.four.data.JacksonNonBlockingObjectMapperFactory$NonBlockingBooleanDeserializer.deserialize(org.codehaus.jackson.JsonParser,DeserializationContext)",this);return (Boolean)o;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.Boolean com.chanapps.four.data.JacksonNonBlockingObjectMapperFactory$NonBlockingBooleanDeserializer.deserialize(org.codehaus.jackson.JsonParser,DeserializationContext)",this,throwable);throw throwable;}
        }
    }

    public static class NonBlockingStringDeserializer extends JsonDeserializer<String> {
        private JsonDeserializer<?> delegate;
        public NonBlockingStringDeserializer() {
            this.delegate = new StdDeserializer.StringDeserializer();
        }
        @Override
        public String deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            com.mijack.Xlog.logMethodEnter("java.lang.String com.chanapps.four.data.JacksonNonBlockingObjectMapperFactory$NonBlockingStringDeserializer.deserialize(org.codehaus.jackson.JsonParser,DeserializationContext)",this,jp,ctxt);try{Object o = null;
            try {
                if (delegate != null)
                    {o = delegate.deserialize(jp, ctxt);}
            }
            catch (Exception e) {
                if (delegate != null)
                    {o = delegate.getNullValue();}
            }
            {com.mijack.Xlog.logMethodExit("java.lang.String com.chanapps.four.data.JacksonNonBlockingObjectMapperFactory$NonBlockingStringDeserializer.deserialize(org.codehaus.jackson.JsonParser,DeserializationContext)",this);return (String)o;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.chanapps.four.data.JacksonNonBlockingObjectMapperFactory$NonBlockingStringDeserializer.deserialize(org.codehaus.jackson.JsonParser,DeserializationContext)",this,throwable);throw throwable;}
        }
    }

    public static class NonBlockingDateDeserializer extends JsonDeserializer<Date> {
        private JsonDeserializer<?> delegate;
        public NonBlockingDateDeserializer() {
            this.delegate = new StdDeserializer.CalendarDeserializer();
        }
        @Override
        public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            com.mijack.Xlog.logMethodEnter("java.util.Date com.chanapps.four.data.JacksonNonBlockingObjectMapperFactory$NonBlockingDateDeserializer.deserialize(org.codehaus.jackson.JsonParser,DeserializationContext)",this,jp,ctxt);try{Object o = null;
            try {
                if (delegate != null)
                    {o = delegate.deserialize(jp, ctxt);}
            }
            catch (Exception e) {
                if (delegate != null)
                    {o = delegate.getNullValue();}
            }
            {com.mijack.Xlog.logMethodExit("java.util.Date com.chanapps.four.data.JacksonNonBlockingObjectMapperFactory$NonBlockingDateDeserializer.deserialize(org.codehaus.jackson.JsonParser,DeserializationContext)",this);return null;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.util.Date com.chanapps.four.data.JacksonNonBlockingObjectMapperFactory$NonBlockingDateDeserializer.deserialize(org.codehaus.jackson.JsonParser,DeserializationContext)",this,throwable);throw throwable;}
        }
    }

    public ObjectMapper createObjectMapper(){
        com.mijack.Xlog.logMethodEnter("ObjectMapper com.chanapps.four.data.JacksonNonBlockingObjectMapperFactory.createObjectMapper()",this);try{ObjectMapper objectMapper = new ObjectMapper();
        {com.mijack.Xlog.logMethodExit("ObjectMapper com.chanapps.four.data.JacksonNonBlockingObjectMapperFactory.createObjectMapper()",this);return objectMapper;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("ObjectMapper com.chanapps.four.data.JacksonNonBlockingObjectMapperFactory.createObjectMapper()",this,throwable);throw throwable;}
    }

}
