package be.nabu.libs.resources.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import be.nabu.libs.resources.api.Resource;
import be.nabu.libs.resources.api.ResourceContainer;
import junit.framework.TestCase;

public class TestResourceIgnoreCalculator extends TestCase {

    // A simple mock ResourceContainer for testing
    private static class MockResourceContainer implements ResourceContainer<Resource> {
        private final String name;
        private final ResourceContainer<?> parent;
        private final List<String> ignoreRules;

        MockResourceContainer(String name, ResourceContainer<?> parent, String... rules) {
            this.name = name;
            this.parent = parent;
            this.ignoreRules = new ArrayList<>(Arrays.asList(rules));
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public ResourceContainer<?> getParent() {
            return parent;
        }

        @Override
        public List<String> getIgnoreRules() {
            return ignoreRules;
        }

        // Unimplemented methods
        @Override
        public Resource getChild(String name) {
            return null;
        }

        @Override
        public Iterator<Resource> iterator() {
            List<Resource> emptyList = Collections.emptyList();
            return emptyList.iterator();
        }

    }

    public void testBasicIgnore() {
        MockResourceContainer root = new MockResourceContainer("root", null, "*.log");
        assertTrue("Should accept other files", ResourceIgnoreCalculator.accept(root, "test.txt"));
        assertFalse("Should ignore .log files", ResourceIgnoreCalculator.accept(root, "test.log"));
    }

    public void testNegation() {
        MockResourceContainer root = new MockResourceContainer("root", null, "*.log", "!important.log");
        assertFalse("Should ignore general .log files", ResourceIgnoreCalculator.accept(root, "test.log"));
        assertTrue("Should accept the negated file", ResourceIgnoreCalculator.accept(root, "important.log"));
    }

    public void testDirectoryIgnore() {
        MockResourceContainer root = new MockResourceContainer("root", null, "build/");
        MockResourceContainer buildDir = new MockResourceContainer("build", root);
        assertFalse("Should ignore the directory itself", ResourceIgnoreCalculator.accept(root, "build"));
        assertFalse("Should ignore files in the directory", ResourceIgnoreCalculator.accept(buildDir, "output.txt"));
    }

    public void testWildcardIgnore() {
        MockResourceContainer root = new MockResourceContainer("root", null, "temp*");
        assertFalse("Should ignore temp1", ResourceIgnoreCalculator.accept(root, "temp1"));
        assertFalse("Should ignore temporary", ResourceIgnoreCalculator.accept(root, "temporary"));
        assertTrue("Should accept other files", ResourceIgnoreCalculator.accept(root, "test.txt"));
    }

    public void testDeepFileIgnore() {
        MockResourceContainer root = new MockResourceContainer("root", null);
        MockResourceContainer src = new MockResourceContainer("src", root, "*.class");
        MockResourceContainer main = new MockResourceContainer("main", src);
        assertFalse("Should ignore .class file in src/main", ResourceIgnoreCalculator.accept(main, "App.class"));
        assertTrue("Should accept .java file in src/main", ResourceIgnoreCalculator.accept(main, "App.java"));
    }

    public void testParentDirectoryRules() {
        MockResourceContainer root = new MockResourceContainer("root", null, "*.bak");
        MockResourceContainer src = new MockResourceContainer("src", root);
        MockResourceContainer main = new MockResourceContainer("main", src, "*.tmp");

        assertFalse("Should ignore .bak file due to root rule", ResourceIgnoreCalculator.accept(main, "test.bak"));
        assertFalse("Should ignore .tmp file due to main rule", ResourceIgnoreCalculator.accept(main, "test.tmp"));
        assertTrue("Should accept other files", ResourceIgnoreCalculator.accept(main, "test.txt"));
    }

    public void testComplexScenario() {
        MockResourceContainer root = new MockResourceContainer("root", null,
                "*.log",
                "!/src/important.log"
        );
        MockResourceContainer src = new MockResourceContainer("src", root,
                "temp/",
                "!temp/keep.txt"
        );
        MockResourceContainer temp = new MockResourceContainer("temp", src);

        assertFalse("Should ignore random log in root", ResourceIgnoreCalculator.accept(root, "random.log"));
        assertTrue("Should accept important.log in src", ResourceIgnoreCalculator.accept(src, "important.log"));
        assertFalse("Should ignore other logs in src", ResourceIgnoreCalculator.accept(src, "other.log"));
        assertFalse("Should ignore file in temp", ResourceIgnoreCalculator.accept(temp, "somefile.txt"));
        assertTrue("Should accept negated file in temp", ResourceIgnoreCalculator.accept(temp, "keep.txt"));
    }
    
    public void testCommentsAndEmptyLines() {
    	MockResourceContainer root = new MockResourceContainer("root", null,
    			"# Ignore logs",
    			"*.log",
    			"",
    			" # Another comment",
    			"!important.log"
    	);
    	assertFalse(ResourceIgnoreCalculator.accept(root, "test.log"));
    	assertTrue(ResourceIgnoreCalculator.accept(root, "important.log"));
    }
    
    public void testAnchoredPath() {
    	MockResourceContainer root = new MockResourceContainer("root", null, "/root.log");
    	MockResourceContainer sub = new MockResourceContainer("sub", root);
    	
    	assertFalse(ResourceIgnoreCalculator.accept(root, "root.log"));
    	assertTrue(ResourceIgnoreCalculator.accept(sub, "root.log"));
    }
}
