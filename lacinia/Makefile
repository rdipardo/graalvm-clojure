.PHONY: all test deps

PROJECT = lacinia
EXE = ./target/$(PROJECT)
JAR = ./target/simple-main.jar

all: test

test: $(EXE)
	$(EXE)

deps: $(JAR)
	-@java -jar -agentlib:native-image-agent=config-output-dir=clinit.d $(JAR)
	@echo
	-ls -ls $$(pwd)/clinit.d

clean:
	-lein clean
	-rm -f $(EXE)

$(JAR):
	@if [ ! -f $@ ]; then lein uberjar; fi

$(EXE): $(JAR)
	@if [ ! -f $@ ]; then lein native; fi
