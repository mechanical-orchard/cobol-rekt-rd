package org.smojol.common.ast;

public enum FlowNodeType {
    ATOMIC,
    COMPOSITE,
    GOTO,
    PERFORM,
    IF_BRANCH,
    DUMMY,
    TRANSFER,
    CALL,
    CONTROL_FLOW,
    PARAGRAPH,
    SECTION,
    CONDITION_CLAUSE,
    SENTENCE,
    IF_YES,
    IF_NO,
    PARAGRAPHS,
    GENERIC_STATEMENT,
    GENERIC_PROCESSING,
    CONDITIONAL_STATEMENT,
    PARAGRAPH_NAME,
    SYMBOL,
    SECTION_HEADER,
    NEXT_SENTENCE,
    SEARCH,
    SEARCH_WHEN,
    AT_END_PHRASE,
    ON_CLAUSE,
    PERFORM_TEST,
    EXIT,
    DISPLAY,
    MOVE,
    ADD,
    COMPUTE,
    SUBTRACT,
    MULTIPLY,
    DIVIDE,
    PROCEDURE_DIVISION_BODY,
    DIALECT_CONTAINER,
    BIND_RUN_UNIT,
    FINISH,
    ON_CLAUSE_ACTION, DIALECT
}
