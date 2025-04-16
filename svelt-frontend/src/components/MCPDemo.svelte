<script>
  import { sendQueryToMCP } from "../services/mcp.js";

  let query = "";
  let mcpResponse = null;
  let loading = false;
  let error = null;

  async function handleSubmit() {
    if (!query.trim()) return;

    try {
      loading = true;
      error = null;
      mcpResponse = await sendQueryToMCP(query);
    } catch (err) {
      error = err.message;
      mcpResponse = null;
    } finally {
      loading = false;
    }
  }
</script>

<div class="card">
  <div class="card-header">
    <h2 class="mb-0">Model Context Protocol (MCP) Demo</h2>
  </div>
  <div class="card-body">
    <p class="mb-4">
      MCP demonstrates how AI systems can provide responses with awareness of
      contextual data. In this demo, the Gemini API will access information
      about your projects as context when answering queries.
    </p>

    <form on:submit|preventDefault={handleSubmit}>
      <div class="mb-3">
        <label for="query" class="form-label">Ask about your projects</label>
        <textarea
          class="form-control"
          id="query"
          rows="3"
          placeholder="Example: What projects do I have? What's the status of my projects? Give me a summary of all projects."
          bind:value={query}
        ></textarea>
      </div>
      <button type="submit" class="btn btn-primary" disabled={loading}>
        {loading ? "Processing..." : "Send Query"}
      </button>
    </form>

    {#if loading}
      <div class="d-flex justify-content-center mt-4">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>
    {:else if error}
      <div class="alert alert-danger mt-4">
        {error}
      </div>
    {:else if mcpResponse}
      <div class="mt-4">
        <h3>MCP Response</h3>

        <div class="card mb-3">
          <div class="card-header bg-info text-white">
            <strong>Query</strong>
          </div>
          <div class="card-body">
            <p>{mcpResponse.query}</p>
          </div>
        </div>

        <div class="card mb-3">
          <div class="card-header bg-success text-white">
            <strong>Response</strong>
          </div>
          <div class="card-body">
            <p style="white-space: pre-line">{mcpResponse.response}</p>
          </div>
        </div>

        <div class="card mb-3">
          <div class="card-header bg-primary text-white">
            <strong>Context Used</strong>
          </div>
          <div class="card-body">
            <ul class="list-group">
              {#each mcpResponse.context as contextItem}
                <li class="list-group-item">{contextItem}</li>
              {/each}
            </ul>
          </div>
        </div>

        <div class="card">
          <div class="card-header bg-secondary text-white">
            <strong>Model Information</strong>
          </div>
          <div class="card-body">
            <p>Model Used: {mcpResponse.modelUsed}</p>
          </div>
        </div>
      </div>
    {/if}
  </div>
</div>
