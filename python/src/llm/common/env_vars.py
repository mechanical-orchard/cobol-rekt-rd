import os

from src.llm.common.console_colors import ConsoleColors


def openai_config():
    c = ConsoleColors()
    open_ai_key: str = os.environ['AZURE_OPENAI_API_KEY']
    open_ai_endpoint: str = os.environ['AZURE_OPENAI_ENDPOINT']
    neo4j_uri: str = os.environ['NEO4J_URI']
    print(c.green(open_ai_endpoint))
    print(c.green(neo4j_uri))
    return open_ai_endpoint, open_ai_key


def neo4j_config():
    c = ConsoleColors()
    neo4j_uri: str = os.environ['NEO4J_URI']
    neo4j_username: str = os.environ['NEO4J_USERNAME']
    neo4j_password: str = os.environ['NEO4J_PASSWORD']
    neo4j_database: str = os.environ['NEO4J_DATABASE'] if os.environ['NEO4J_DATABASE'] else "neo4j"
    print(c.green(neo4j_uri))
    return neo4j_uri, neo4j_username, neo4j_password, neo4j_database
