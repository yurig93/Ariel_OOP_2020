package ex1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NodeInfoTest {
    @Test
    void testDefaultValues(){
        node_info n = new WGraph_DS().new NodeInfo();
        assertEquals("", n.getInfo());
        assertEquals(0, n.getTag());
    }
}