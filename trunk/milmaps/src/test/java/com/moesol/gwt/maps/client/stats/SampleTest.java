package com.moesol.gwt.maps.client.stats;

import static org.junit.Assert.*;

import org.junit.Test;

public class SampleTest {

	@Test
	public void testCallTree() {
		Sample.MAP_UPDATE_VIEW.beginSample();
		Sample.MAP_UPDATE_VIEW.endSample();
		
		assertEquals(1, Sample.ROOT.getChildren().size());
		assertTrue(Sample.ROOT.getChildren().contains(Sample.MAP_UPDATE_VIEW));
	}

	@Test
	public void testCallTree2() {
		Sample.MAP_UPDATE_VIEW.beginSample();
		Sample.LAYER_UPDATE_VIEW.beginSample();
		Sample.LAYER_UPDATE_VIEW.endSample();
		Sample.LAYER_POSITION_IMAGES.beginSample();
		Sample.LAYER_POSITION_IMAGES.endSample();
		Sample.MAP_UPDATE_VIEW.endSample();
		
		Sample.MAP_UPDATE_VIEW.beginSample();
		Sample.LAYER_UPDATE_VIEW.beginSample();
		Sample.LAYER_UPDATE_VIEW.endSample();
		Sample.LAYER_POSITION_IMAGES.beginSample();
		Sample.LAYER_POSITION_IMAGES.endSample();
		Sample.MAP_UPDATE_VIEW.endSample();
		
		assertEquals(1, Sample.ROOT.getChildren().size());
		assertTrue(Sample.ROOT.getChildren().contains(Sample.MAP_UPDATE_VIEW));

		assertEquals(2, Sample.MAP_UPDATE_VIEW.getChildren().size());
		assertTrue(Sample.MAP_UPDATE_VIEW.getChildren().contains(Sample.LAYER_UPDATE_VIEW));
		assertTrue(Sample.MAP_UPDATE_VIEW.getChildren().contains(Sample.LAYER_POSITION_IMAGES));
		
		depthFirstTraversal();
	}

	private void depthFirstTraversal() {
		traverse(0, Sample.ROOT);
	}

	private void traverse(int indent, Sample node) {
		for (Sample s : node.getChildren()) {
			outLeader(indent);
			System.out.println(s);
			traverse(indent + 1, s);
		}
	}

	private void outLeader(int indent) {
		for (int i = 0; i < indent; i++) {
			System.out.print('-');
		}
	}
	
}
