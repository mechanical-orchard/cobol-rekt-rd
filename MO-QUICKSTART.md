### Getting setup to use this repo

## Initialize direnv
direnv allow

## Pull depenedencies and setup machine
Run ./scripts/dev.sh to install the necessary prerequistes

## Example commands
Run the given example code
export COBOL_REKT_LOCATION=<your path to cobol rekt>
```java
java -jar smojol-cli/target/smojol-cli.jar run test-exp.cbl hello.cbl --commands="WRITE_FLOW_AST INJECT_INTO_NEO4J EXPORT_TO_GRAPHML WRITE_RAW_AST DRAW_FLOWCHART WRITE_CFG" --srcDir $COBOL_REKT_LOCATION/smojol-test-code --copyBooksDir $COBOL_REKT_LOCATION/smojol-test-code --dialectJarPath ./che-che4z-lsp-for-cobol-integration/server/dialect-idms/target/dialect-idms.jar --reportDir out/report --generation=PROGRAM
```


Run card demo for CBACT01C.cbl
export CARD_DEMO_LOCATION=<your path to card demo>
```java
java -jar smojol-cli/target/smojol-cli.jar run CBACT01C.cbl --commands="WRITE_FLOW_AST INJECT_INTO_NEO4J EXPORT_TO_GRAPHML WRITE_RAW_AST DRAW_FLOWCHART WRITE_CFG" --srcDir $CARD_DEMO_LOCATION/app/cbl --copyBooksDir $CARD_DEMO_LOCATION/app/cpy --dialectJarPath ./che-che4z-lsp-for-cobol-integration/server/dialect-idms/target/dialect-idms.jar --reportDir out/report --generation=PROGRAM
```

Card demo analysis for all source
```java
java -jar smojol-cli/target/smojol-cli.jar run $(ls $CARD_DEMO_LOCATION/app/cbl/*.cbl | xargs basename) --commands="WRITE_FLOW_AST INJECT_INTO_NEO4J EXPORT_TO_GRAPHML WRITE_RAW_AST DRAW_FLOWCHART WRITE_CFG" --srcDir $CARD_DEMO_LOCATION/app/cbl --copyBooksDir workspace/aws-mainframe-modernization-carddemo/app/cpy --dialectJarPath ./che-che4z-lsp-for-cobol-integration/server/dialect-idms/target/dialect-idms.jar --reportDir out/report --generation=PROGRAM
```

## Helpful Commands
Delete all nodes from the graph
```cypherl
MATCH (n)
DETACH DELETE n
```

View the schema
```cypherl
CALL db.schema.visualization();
```