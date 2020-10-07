package com.quirk.csv.processor;

import com.quirk.csv.exception.UninstantiableException;
import com.quirk.csv.wrappers.read.ReadWrapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Does the processing for {@link com.quirk.csv.annotation.CSVReadComponent} whose type uses ORDER
 */
@SuppressWarnings("rawtypes")
class CSVOrderReadProcessor<T> extends AbstractCSVReadProcessor<T> {

    private final static Logger LOGGER = Logger.getLogger(CSVOrderReadProcessor.class.getName());

    public CSVOrderReadProcessor(Class<T> parsedClazz,
                                 Map<Class, ReadWrapper> wrapperMap) {
        super(parsedClazz, wrapperMap);

    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<T> read(Reader reader, CSVFormat format) throws IOException {
        List<T> items = new ArrayList<>();
        Map<Integer, CSVReadAnnotationManager> map = this.csvReadAnnotationManagers.stream().collect(Collectors.toMap((CSVReadAnnotationManager c) -> c.getOrder(), Function.identity()));

        try (CSVParser parser = new CSVParser(reader, format);) {
            for (CSVRecord record : parser) {
                Object obj = parsedClazz.newInstance();
                for (Entry<Integer, CSVReadAnnotationManager> entry : map.entrySet()) {
                    CSVReadAnnotationManager cm = entry.getValue();
                    int order = entry.getKey();

                    try {
                        cm.setValue(obj, record.get(order), setValueMap);
                    } catch (IllegalArgumentException e) {
                        LOGGER.log(Level.SEVERE, "Failed for order#: " + order + ". See " + parsedClazz+"#"+cm.generateReference(), e);
                    } catch (InvocationTargetException e) {
                        LOGGER.log(Level.SEVERE, "Failed for order#: " + order+ ". See " + parsedClazz+"#"+cm.generateReference(), e);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        LOGGER.log(Level.WARNING, "Order#: " + order + " exceeds the number of values for the row", e);
                    }

                }
                items.add((T) obj);

            }
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.log(Level.SEVERE,
                    "Could not create object. Check to make sure the you have a visible default constructor", e);
            throw new UninstantiableException(
                    "Could not create object. Check to make sure the you have a visible default constructor", e);
        }
        return items;
    }
}
