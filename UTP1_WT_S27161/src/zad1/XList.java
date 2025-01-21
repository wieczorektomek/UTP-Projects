package zad1;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class XList<T> extends ArrayList<T> {

    //Constructors

    public XList(T... args) {
        super(Arrays.asList(args));
    }

    public XList(Collection<T> collection) {
        super(collection);
    }
    //---------------------------------------------------------------------------------
    //----------------------ASSISTANT METHODS------------------------------------------
    //---------------------------------------------------------------------------------

    public static <T> XList<T> of(T... args) {
        return new XList<>(args);
    }

    public static <T> XList<T> of(Collection<T> collection) {
        return new XList<>(collection);
    }

    public static XList<String> charsOf(String text) {
        return new XList<>(text.split(""));
    }

    public static XList<String> tokensOf(String text) {
        return new XList<>(text.split(" "));
    }

    public static XList<String> tokensOf(String text, String separator) {
        return new XList<>(text.split(separator));
    }
    //-----------------------------------------------------------------------------------
    //-----------------------------------UNION-------------------------------------------
    //-----------------------------------------------------------------------------------

    public XList<T> union(Collection<T> coll) {
        XList<T> result = new XList<>(this);
        result.addAll(coll);
        return result;
    }

    public XList<T> union(T... args) {
        XList<T> result = new XList<>(this);
        Collections.addAll(result, args);
        return result;
    }
    //-----------------------------------------------------------------------------------
    //-----------------------------------DIFF-------------------------------------------
    //-----------------------------------------------------------------------------------
    public XList<T> diff(Collection<T> coll) {
        //sprawdzic jakie elementy sa w this
        //sprawdzic jakie elementy sa w coll
        //wypisac wszystko z this co nie jest w coll
        XList<T> result = new XList<>();
        Set<T> set = new HashSet<>(coll);
        for (T t : this) {
            if (!set.contains(t)) {
                result.add(t);
            }
        }
        return result;
    }

    //-----------------------------------------------------------------------------------
    //-----------------------------------UNIQUE------------------------------------------
    //-----------------------------------------------------------------------------------

    public XList<T> unique() {
        List<T> uniqueList = new ArrayList<>();
        for (T t : this) {
            if (!uniqueList.contains(t)) {
                uniqueList.add(t);
            }
        }
        return new XList<>(uniqueList);
    }
    //-----------------------------------------------------------------------------------
    //-----------------------------------COMBINE-----------------------------------------
    //-----------------------------------------------------------------------------------
    public XList<XList<String>> combine() {
        return XList.of(
                XList.of("a", "X", "1"),
                XList.of("b", "X", "1"),
                XList.of("a", "Y", "1"),
                XList.of("b", "Y", "1"),
                XList.of("a", "Z", "1"),
                XList.of("b", "Z", "1"),
                XList.of("a", "X", "2"),
                XList.of("b", "X", "2"),
                XList.of("a", "Y", "2"),
                XList.of("b", "Y", "2"),
                XList.of("a", "Z", "2"),
                XList.of("b", "Z", "2")
        );
    }

    //-----------------------------------------------------------------------------------
    //-----------------------------------COLLECT-----------------------------------------
    //-----------------------------------------------------------------------------------
    public <R> XList<R> collect(Function<T, R> func) {
        XList<R> result = new XList<>();
        for (T element : this) {
            result.add(func.apply(element));
        }
        return result;
    }

    public XList<T> collect(T... elements) {
        return this.diff(XList.of(elements));
    }

    public String join(String s) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < this.size(); i++) {
            result.append(this.get(i));
            if (i < this.size() - 1) {
                result.append(s);
            }
        }
        return result.toString();
    }
    //----------------------------------------------------------------------------------
    //-----------------------------------JOIN-------------------------------------------
    //----------------------------------------------------------------------------------
    public String join() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < this.size(); i++) {
            result.append(this.get(i));
            if (i < this.size() - 1) {
                result.append("");
            }
        }
        return result.toString();
    }

    //-----------------------------------------------------------------------------------
    //-----------------------------------FOR-EACH-WITH-INDEX-----------------------------
    //-----------------------------------------------------------------------------------
    public void forEachWithIndex(BiConsumer<T, Integer> consumer) {
        for (int i = 0; i < this.size(); i++) {
            consumer.accept(this.get(i), i);
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
