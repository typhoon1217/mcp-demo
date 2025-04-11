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
      MCP는 AI 시스템이 맥락 데이터를 인식하여 응답을 제공하는 방법을
      보여줍니다. 이 데모에서는 Gemini API가 쿼리에 답변할 때 프로젝트에 관한
      정보를 맥락으로 활용합니다.
    </p>

    <form on:submit|preventDefault={handleSubmit}>
      <div class="mb-3">
        <label for="query" class="form-label"
          >프로젝트에 대해서 물어보세요!</label
        >
        <textarea
          class="form-control"
          id="query"
          rows="3"
          placeholder="예시: 내 프로젝트는 무엇이 있나요? 프로젝트 상태는 어떤가요? 모든 프로젝트에 대한 요약을 보여주세요."
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
            <strong>질문</strong>
          </div>
          <div class="card-body">
            <p>{mcpResponse.query}</p>
          </div>
        </div>

        <div class="card mb-3">
          <div class="card-header bg-success text-white">
            <strong>응답</strong>
          </div>
          <div class="card-body">
            <p style="white-space: pre-line">{mcpResponse.response}</p>
          </div>
        </div>

        <div class="card mb-3">
          <div class="card-header bg-primary text-white">
            <strong>컨텍스트</strong>
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
            <strong>Model 정보</strong>
          </div>
          <div class="card-body">
            <p>Model: {mcpResponse.modelUsed}</p>
          </div>
        </div>
      </div>
    {/if}
  </div>
</div>
