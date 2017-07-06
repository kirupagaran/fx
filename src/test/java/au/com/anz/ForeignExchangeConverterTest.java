package au.com.anz;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Created by kirupa on 06/07/17.
 */


/**
 * Unit test for simple App.
 */
public class ForeignExchangeConverterTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ForeignExchangeConverterTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ForeignExchangeConverterTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
}
