# Embabel Customer Service Agent

[![Java CI with Gradle](https://github.com/habuma/embabel-customer-service-agent/actions/workflows/gradle.yml/badge.svg)](https://github.com/habuma/embabel-customer-service-agent/actions/workflows/gradle.yml)

The agent uses the GPT-OSS model running on a local Ollama. Therefore, before running
the agent, be sure to install Ollama and the GPT-OSS model (if you've not done so already).

This is an Embabel-based customer service agent. Run it as you would run any Spring Boot
application. For example, using the Gradle wrapper:

```sh
./gradlew bootRun
```

Once the agent is running, it will expose an MCP server on port 8080. You can poke at this
MCP server using the [MCP inspector](https://modelcontextprotocol.io/docs/tools/inspector)
(using the Streamable HTTP protocol at http://localhost:8080/mcp) or you can configure your favorite
LLM/Agent UI to use the MCP server.

To configure it in Claude Desktop, you'll need to run it through the MCP-Remote MCP server
which proxies through a local MCP server (since Claude Desktop does not yet support remote
MCP). To do that, add the following configuration to your Claude Desktop configuration:

```yaml
{
  "mcpServers": {
    "customer-service": {
      "command": "npx",
      "args": [
        "-y",
        "mcp-remote",
        "http://localhost:8080/mcp"
      ]
    }
  }
}
```

Then start a chat via the UI. You can try things like:

- My order arrived damaged. I want a refund.
- I ordered a red sweater, but received a green sweater. Can you please help?
- My order is a week late in arriving. Please help me.

When asked for an order number, numbers "112233", "223344", and "334455" are the order numbers
for three orders available in the database.

Note that (for the most part) the resolution that the agent arrives at is usually pretty
good. But, at this point I didn't spend much effort defining policies for the agent to
follow. As a result, it kinda wings it and sometimes responds with unusual resolutions.
Maybe I'll provide policies at some point in the future.
