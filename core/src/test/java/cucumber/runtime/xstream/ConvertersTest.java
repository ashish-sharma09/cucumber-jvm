package cucumber.runtime.xstream;

import cucumber.deps.com.thoughtworks.xstream.converters.ConverterLookup;
import cucumber.deps.com.thoughtworks.xstream.converters.SingleValueConverter;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConvertersTest {
    @Test
    public void shouldTransformToTheRightType() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        LocalizedXStreams transformers = new LocalizedXStreams(classLoader);

        ConverterLookup en = transformers.get(Locale.US).getConverterLookup();
        assertTrue((Boolean) ((SingleValueConverter) en.lookupConverterForType(Boolean.class)).fromString("true"));
        assertTrue((Boolean) ((SingleValueConverter) en.lookupConverterForType(Boolean.TYPE)).fromString("true"));
        assertEquals(3000.15f, (Float) ((SingleValueConverter) en.lookupConverterForType(Float.class)).fromString("3000.15"), 0.000001);
        assertEquals(3000.15f, (Float) ((SingleValueConverter) en.lookupConverterForType(Float.TYPE)).fromString("3000.15"), 0.000001);
        assertEquals(new BigDecimal("3000.15"), ((SingleValueConverter) en.lookupConverterForType(BigDecimal.class)).fromString("3000.15"));

        ConverterLookup no = transformers.get(new Locale("no")).getConverterLookup();
        assertEquals(3000.15f, (Float) ((SingleValueConverter) no.lookupConverterForType(Float.TYPE)).fromString("3000,15"), 0.000001);
    }

    @Test
    public void shouldTransformPatternWithFlags() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        LocalizedXStreams transformers = new LocalizedXStreams(classLoader);

        ConverterLookup en = transformers.get(Locale.US).getConverterLookup();
        Pattern expected = Pattern.compile("hello", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Pattern actual = (Pattern) ((SingleValueConverter) en.lookupConverterForType(Pattern.class)).fromString("/hello/im");
        assertEquals(expected.pattern(), actual.pattern());
        assertEquals(expected.flags(), actual.flags());
    }

    @Test
    public void shouldTransformPatternWithoutFlags() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        LocalizedXStreams transformers = new LocalizedXStreams(classLoader);

        ConverterLookup en = transformers.get(Locale.US).getConverterLookup();
        Pattern expected = Pattern.compile("hello");
        Pattern actual = (Pattern) ((SingleValueConverter) en.lookupConverterForType(Pattern.class)).fromString("hello");
        assertEquals(expected.pattern(), actual.pattern());
        assertEquals(expected.flags(), actual.flags());
    }

    @Test
    public void shouldIncludeSlashesInPatternWhenThereAreNoFlags() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        LocalizedXStreams transformers = new LocalizedXStreams(classLoader);

        ConverterLookup en = transformers.get(Locale.US).getConverterLookup();
        Pattern expected = Pattern.compile("/hello/");
        Pattern actual = (Pattern) ((SingleValueConverter) en.lookupConverterForType(Pattern.class)).fromString("/hello/");
        assertEquals(expected.pattern(), actual.pattern());
        assertEquals(expected.flags(), actual.flags());
    }

    @Test
    public void shouldTransformToTypeWithStringCtor() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        LocalizedXStreams transformers = new LocalizedXStreams(classLoader);
        ConverterLookup en = transformers.get(Locale.US).getConverterLookup();
        SingleValueConverter c = (SingleValueConverter) en.lookupConverterForType(MyClass.class);
        assertEquals("X", ((MyClass) c.fromString("X")).s);
    }

    @Test
    public void shouldTransformToTypeWithObjectCtor() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        LocalizedXStreams transformers = new LocalizedXStreams(classLoader);
        ConverterLookup en = transformers.get(Locale.US).getConverterLookup();
        SingleValueConverter c = (SingleValueConverter) en.lookupConverterForType(MyOtherClass.class);
        assertEquals("X", ((MyOtherClass) c.fromString("X")).o);
    }

    public static class MyClass {
        public final String s;

        public MyClass(String s) {
            this.s = s;
        }
    }

    public static class MyOtherClass {
        public final Object o;

        public MyOtherClass(Object o) {
            this.o = o;
        }
    }
}
