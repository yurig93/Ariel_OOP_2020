package ex1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WGraph_DS_TestCustom {
    @Test
    void testCopy() {
        weighted_graph g = new WGraph_DS();
        g.addNode(0);
        g.addNode(1);
        g.addNode(1);

        weighted_graph g2 = new WGraph_DS(g);
        assertEquals(g, g2);
    }

}
