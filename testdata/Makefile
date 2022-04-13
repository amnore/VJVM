SRCS := $(shell find src -name '*.java' | sed 's,^src/,,')
CLASSES := $(SRCS:.java=.class)
LAB1_JARS := $(shell find src/lab1 -name '.mkjar' | sed 's,^src/,,')
LAB1_JDK_CLASSES := java/lang/String

all: clean lab1
compile: $(addprefix build/,$(CLASSES))

lab1: lab1-dump lab1-jar
lab1-jar: $(addprefix build/,$(LAB1_JARS:/.mkjar=.jar))

lab1-dump: compile
ifeq ($(VJVM_JAR),)
	$(error VJVM_JAR not set)
endif
	@for i in $(LAB1_JDK_CLASSES) $$(find build/lab1 -type f | sed -E -e 's,^build/(.+)\.class,\1,'); do \
		mkdir -p dump/$$(dirname $$i); \
		echo "java -jar $(VJVM_JAR) -cp build dump $${i//\//.} > dump/$$i.dump"; \
		java -jar $(VJVM_JAR) -cp build dump $${i//\//.} > dump/$$i.dump; \
	done

build/%.class: src/%.java
	@mkdir -p build
	javac -source 8 -d build $<

build/lab1/%.jar: compile
	jar -cf $@ -C build $(patsubst build/lab1/%.jar,lab1/%,$@)
	rm -rf $(@:.jar=)

clean:
	rm -rf build dump

.PHONY: clean
