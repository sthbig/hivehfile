package cn.jiguang.hivehfile.util;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;

/**
 * Created by fitz
 * Date: 2017/5/16
 * <p>
 * Description:
 */
public class MapUtilTest {
    @Test
    public void testConvertStringToMap(){
        String input = "{'data_date':'20170101','symbol1':'feature=install_app/data_date=20170505,feature=uninstall_app/data_date=20170501'}";
        HashMap<String,String> expected = new HashMap<String, String>();
        expected.put("data_date","20170101");
        expected.put("symbol1","feature=install_app/data_date=20170505,feature=uninstall_app/data_date=20170501");
        HashMap<String,String> actual = MapUtil.convertStringToMap(input);
        assertEquals(expected,actual);
    }
}
