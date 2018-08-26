package test.gb.server;

import gb.server.AuthService;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;

public class Test1 {

    @Test
    public void test2(){

    }

    @Test
    public void test1() {
        try {
            Assert.assertEquals("nick1", AuthService.testConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
