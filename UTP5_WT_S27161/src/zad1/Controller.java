package zad1;

import zad1.models.Bind;
import javax.script.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Controller implements Bind {
    ScriptEngineManager manager;
    ScriptEngine engine;
    String modelName = "";
    public Class<?> modelClass;
    public Object model;
    Map<String, double[]> dataMap = new HashMap<String, double[]>();
    Map<String, double[]> resultDataMap = new LinkedHashMap<String, double[]>();
    int size = 15;
    int LL;
    double EKS[] = {2, 4, 6, 8, 10};
    double PKB[] = {2, 2, 2, 2, 2};
    List<String> finalResults[];
    Field[] fields;

    public Controller() {
    }

    /**
     * Writes data from dataMap to model fields marked with @Bind annotation
     */
    public void writeModelFields() {
        for (Field field : fields) {
            if (field.isAnnotationPresent(Bind.class) && !field.getName().equals("LL")) {
                field.setAccessible(true);
                try {
                    field.set(model, dataMap.get(field.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if (field.isAnnotationPresent(Bind.class) && field.getName().equals("LL")) {
                field.setAccessible(true);
                try {
                    field.set(model, LL);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Reads values from model fields marked with @Bind annotation into resultDataMap
     */
    public void readModelFields() {
        double[] arr = new double[LL];
        for (Field field : fields) {
            if (field.isAnnotationPresent(Bind.class) && !field.getName().equals("LL")) {
                field.setAccessible(true);
                try {
                    arr = (double[]) field.get(model);
                    resultDataMap.put(field.getName(), arr);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Controller(String modelName) {
        this.modelName = modelName;
        findClass();
        manager = new ScriptEngineManager();
        engine = manager.getEngineByName("groovy");
    }

    /**
     * Finds and instantiates model class based on modelName
     */
    public void findClass() {
        try {
            modelClass = Class.forName("zad1.models." + modelName);
            this.model = modelClass.newInstance();
            fields = modelClass.getDeclaredFields();
        } catch (ClassNotFoundException e) {
            System.out.println("Model not found");
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Executes model's run method using reflection
     */
    public Controller runModel() {
        writeModelFields();
        Method run;
        try {
            run = modelClass.getDeclaredMethod("run");
            run.setAccessible(true);
            run.invoke(model);
        } catch (NoSuchMethodException e) {
            System.out.println("There is no \"run()\" method in this model");
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        readModelFields();
        return this;
    }

    /**
     * Reads data from file into dataMap
     */
    public Controller readDataFrom(String s) {
        String[] lineValues;
        double[] values;
        size = 15;
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(s));
            String line = reader.readLine();
            while (line != null) {
                lineValues = line.split("\\s");
                if (lineValues[0].equals("LATA")) {
                    size = lineValues.length - 1;
                }
                values = new double[size];
                for (int i = 1; i < size + 1; i++) {
                    if (i < lineValues.length) {
                        values[i - 1] = Double.parseDouble(lineValues[i]);
                    }
                    if (lineValues.length - 1 < size && i >= lineValues.length) {
                        values[i - 1] = values[i - 2];
                    }
                }
                dataMap.put(lineValues[0], values);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        values = dataMap.get("LATA");
        resultDataMap.put("LATA", values);
        LL = values.length;
        return this;
    }

    /**
     * Executes provided script with current context
     */
    public Controller runScript(String script) {
        try {
            Bindings bindings = new SimpleBindings();
            bindings.put("LL", LL);
            for (Map.Entry<String, double[]> entry : resultDataMap.entrySet()) {
                bindings.put(entry.getKey(), entry.getValue());
            }
            Object result = engine.eval(script, bindings);
            for (String key : bindings.keySet()) {
                Object value = bindings.get(key);
                if (value instanceof double[] && !key.matches("[a-z]") && !resultDataMap.containsKey(key)) {
                    resultDataMap.put(key, (double[]) value);
                }
            }
            return this;
        } catch (ScriptException e) {
            System.err.println("Błąd wykonania skryptu: " + e.getMessage());
            return this;
        }
    }

    /**
     * Reads and executes script from file
     */
    public Controller runScriptFromFile(String fileName) {
        try {
            String script = new String(Files.readAllBytes(Paths.get(fileName)));
            return runScript(script);
        } catch (IOException e) {
            System.err.println("Błąd odczytu pliku skryptu: " + e.getMessage());
            return this;
        }
    }

    /**
     * Updates resultDataMap with current script engine bindings
     */
    private void showBindings() {
        ScriptContext ctx = engine.getContext();
        List<Integer> scopes = ctx.getScopes();
        for (Integer scope : scopes) {
            Bindings bnd = ctx.getBindings(scope);
            for (String key : bnd.keySet()) {
                if(!resultDataMap.containsKey(key) && !key.equals("LL") && bnd.get(key).getClass().toString().equals("class [D"))
                    resultDataMap.put(key, (double[])bnd.get(key));
            }
        }
    }

    /**
     * Returns results formatted as TSV string
     */
    public String getResultsAsTsv() {
        StringBuilder sb = new StringBuilder();
        String helpS = "";
        double[] vars = new double[size];
        for (Map.Entry<String, double[]> map : resultDataMap.entrySet()) {
            sb.append(map.getKey());
            vars = resultDataMap.get(map.getKey());
            for (int i = 0; i < vars.length; i++) {
                if (map.getKey().equals("LATA")) {
                    helpS = vars[i] + "";
                    helpS = helpS.replace(".0","");
                    sb.append("\t" + helpS);
                } else {
                    sb.append("\t" + vars[i]);
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }

    public Map<String, double[]> getResultDataMap() {
        return resultDataMap;
    }
}