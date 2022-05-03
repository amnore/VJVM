SRCS := $(shell find src -name '*.java' | sed 's,^src/,,')
CLASSES := $(SRCS:.java=.class)

LAB1_JARS := $(shell find src/lab1 -name '.mkjar' | sed 's,^src/,,')
LAB1_JDK_CLASSES := java/lang/String

all: clean check-vjvm-jar lab1 lab2
compile: $(addprefix build/,$(CLASSES))

check-vjvm-jar:
ifeq ($(VJVM_JAR),)
	$(error VJVM_JAR not set)
endif

lab1: lab1-dump lab1-jar
lab1-jar: $(addprefix build/,$(LAB1_JARS:/.mkjar=.jar))

lab1-dump: compile
	@for i in $(LAB1_JDK_CLASSES) $$(find build/lab1 -type f | sed -E -e 's,^build/(.+)\.class,\1,'); do \
		mkdir -p dump/$$(dirname $$i); \
		echo "java -jar $(VJVM_JAR) -cp build dump $${i//\//.} > dump/$$i.dump"; \
		java -jar $(VJVM_JAR) -cp build dump $${i//\//.} > dump/$$i.dump; \
	done

lab2:
	@for i in $$(find build/lab2 -type f | sed -E -e 's,^build/(.+)\.class,\1,'); do \
		mkdir -p dump/$$(dirname $$i); \
		echo "java -jar $(VJVM_JAR) -cp build run $${i//\//.} < input/$$i.in > dump/$$i.dump"; \
		java -jar $(VJVM_JAR) -cp build run $${i//\//.} < input/$$i.in > dump/$$i.dump; \
	done

build/%.class: src/%.java
	@mkdir -p build
	javac -sourcepath src -source 8 -target 8 -d build $<

build/lab1/%.jar: compile
	jar -cf $@ -C build $(patsubst build/lab1/%.jar,lab1/%,$@)
	rm -rf $(@:.jar=)

clean:
	rm -rf build dump

.PHONY: clean
