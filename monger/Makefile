.PHONY: all test deps

PROJECT = monger
EXE = ./target/$(PROJECT)
JAR = ./target/simple-main.jar

all: test

test: $(EXE) db
	-$(EXE)

deps: $(JAR) db
	-@java -jar -agentlib:native-image-agent=config-output-dir=clinit.d $(JAR)
	@echo
	-ls -ls $$(pwd)/clinit.d

db:
	-@docker-compose up -d --force-recreate
	@echo "Waiting for database . . ."
	@while [ $$(docker container ls -a --format '{{.Names}}' | grep -c '$(PROJECT)') -eq 0 ]; do sleep 20; done

clean:
	-lein clean
	-rm -f $(EXE)

$(JAR):
	@if [ ! -f $@ ]; then lein uberjar; fi

$(EXE): $(JAR)
	@if [ ! -f $@ ]; then lein native; fi
