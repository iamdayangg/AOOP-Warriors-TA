/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorphapps.config;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies DBConnection can actually reach motorph_db.
 * If this test fails, nothing else in the suite has a chance —
 * every DAO/Service test below depends on this connection working.
 *
 * @author DAYANG GWAPA
 */
class DBConnectionTest {

    @Test
    void getConnection_returnsLiveConnectionToMotorphDb() throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            assertNotNull(conn, "getConnection() should never return null");
            assertFalse(conn.isClosed(), "Connection should be open right after creation");
            assertTrue(conn.isValid(2), "Connection should respond to a 2-second validity check");
            assertEquals("motorph_db", conn.getCatalog(),
                    "Connection should be pointed at motorph_db specifically");
        }
    }
}
