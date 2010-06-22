package me.prettyprint.cassandra.utils;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Date: 2010-06-16
 * Time: 06:39:32
 */
public class StringTimeStampTest {


    @Before
    public void setUp() {

    }

    @Test
    public void shouldDateBeConvertedToString() {
        //given
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2010, 05, 10, 01, 00, 00);
        String expectedDate = "2010-06-10T01:00:00.000+0200";

        //when
        StringTimeStamp timestamp = new StringTimeStamp(cal.getTime());

        //then
        assertEquals(expectedDate, timestamp.getDateString());
    }


    @Test
    public void shouldDateStringBeConvertedToDate() {
        //given
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2010, 05, 10, 01, 00, 00);

        String dateString = "2010-06-10T01:00:00.000+0200";

        //when
        StringTimeStamp timestamp = new StringTimeStamp(dateString);

        //then
        assertTrue(cal.getTimeInMillis() == timestamp.getDate().getTime());
    }

    @Test
    public void shouldConverionBeBirectional() {
        //given
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2010, 05, 10, 01, 00, 00);

        //when
        String dateString = new StringTimeStamp(cal.getTime()).getDateString();
        Date date = new StringTimeStamp(dateString).getDate();

        //then
        assertTrue(cal.getTime().getTime() == date.getTime());
    }


    @Test(expected = IllegalArgumentException.class)
    public void shouldExceptionBeThrownWhenPassingNullDate() {
        //when
        new StringTimeStamp((Date) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldExceptionBeThrownWhenPassingNullString() {
        //when
        new StringTimeStamp((String) null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void shouldExceptionBeThrownWhenPassingEmptyString() {
        //when
        new StringTimeStamp("  ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldExceptionBeThrownWhenPassingIllegalFormatString() {
        //when
        new StringTimeStamp("123zxc");
    }


}
