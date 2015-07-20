package ramd.api;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Schema {

    public static Schema build(String[] colnames) throws Exception {
        return new Schema(colnames);
    }

    Map<String, Integer> _schema;


    private Gson _gson;
    private String[][] _tab;
    private int _nr;

    private Schema(String[] colnames) {
        _schema = new HashMap<String, Integer>();
        for (int i = 0; i < colnames.length; i++)
            _schema.put(colnames[i], i);

        _tab = new String[1][];
        _nr = 0;
    }


    /**
     * Add row to an input/output schema. Ownership of the String array is transferred
     * to the Schema.
     * @param row row to add to Schema
     * @return this Schema
     * @throws Exception
     */
    public Schema addRow(String[] row) throws Exception {
        if (row == null) return this;
        if (row.length != _schema.size())
            throw new Exception("Row does not match Schema size.");

        _tab[_nr++] = row;
        if (_tab.length == _nr)
            _tab = Arrays.copyOf(_tab, Math.max(1, _nr<<2));

        return  this;
    }


    public String toJson() {
        if (_gson == null) _gson = new Gson();
        return "{ \"ip\" : \"localhost\", \"port\" : \"23456\"}";
//        return _gson.toJson(this);
    }
}
