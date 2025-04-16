<script>
  import { onMount } from "svelte";
  import { getAllProjects } from "../services/api.js";

  let query = "";
  let mcpResponse = null;
  let loading = false;
  let error = null;
  let projects = [];

  // Function to call the MCP Action API
  async function processActionRequest() {
    if (!query.trim()) return;

    try {
      loading = true;
      error = null;

      const response = await fetch("http://localhost:8080/api/mcp/action", {
        method: "POST",
        headers: {
          "Content-Type": "text/plain",
        },
        body: query,
      });

      if (!response.ok) {
        throw new Error(`Error: ${response.status}`);
      }

      mcpResponse = await response.json();

      // Refresh projects list after action
      await loadProjects();
    } catch (err) {
      error = err.message;
      mcpResponse = null;
    } finally {
      loading = false;
    }
  }

  async function loadProjects() {
    try {
      projects = await getAllProjects();
    } catch (err) {
      console.error("Error loading projects:", err);
    }
  }

  onMount(loadProjects);
</script>

<div class="card">
  <div class="card-header">
    <h2 class="mb-0">AI-Powered Project Management</h2>
  </div>
  <div class="card-body">
    <p class="mb-4">
      Use natural language to manage your projects. The AI will interpret your
      request and perform the appropriate action.
    </p>

    <div class="mb-4">
      <h4>Example Commands:</h4>
      <ul>
        <li>
          "Create a new project called 'Website Redesign' with a status of
          'Pending'"
        </li>
        <li>"List all projects"</li>
        <li>"Show me project with ID 1"</li>
        <li>"Update project 2 and set status to 'Active'"</li>
        <li>"Delete project 3"</li>
        <li>"How many projects are active?"</li>
      </ul>
    </div>

    <form on:submit|preventDefault={processActionRequest}>
      <div class="mb-3">
        <label for="query" class="form-label">What would you like to do?</label>
        <textarea
          class="form-control"
          id="query"
          rows="3"
          placeholder="Type your command in natural language..."
          bind:value={query}
        ></textarea>
      </div>

      <button type="submit" class="btn btn-primary" disabled={loading}>
        {loading ? "Processing..." : "Execute"}
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
        <h3>Action Result</h3>

        <div class="card mb-3">
          <div class="card-header bg-info text-white">
            <strong>Your Request</strong>
          </div>
          <div class="card-body">
            <p>{mcpResponse.query}</p>
          </div>
        </div>

        <div class="card mb-3">
          <div class="card-header bg-success text-white">
            <strong>Result</strong>
          </div>
          <div class="card-body">
            <p style="white-space: pre-line">{mcpResponse.response}</p>
          </div>
        </div>
      </div>
    {/if}

    <div class="mt-5">
      <h3>Current Projects</h3>
      {#if projects.length === 0}
        <p class="text-muted">No projects found. Try creating one!</p>
      {:else}
        <div class="table-responsive">
          <table class="table table-striped">
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Description</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {#each projects as project}
                <tr>
                  <td>{project.id}</td>
                  <td>{project.name}</td>
                  <td>{project.description || "-"}</td>
                  <td>
                    <span
                      class="badge {project.status === 'Active'
                        ? 'bg-success'
                        : project.status === 'Pending'
                          ? 'bg-warning'
                          : 'bg-secondary'}"
                    >
                      {project.status}
                    </span>
                  </td>
                </tr>
              {/each}
            </tbody>
          </table>
        </div>
      {/if}
    </div>
  </div>
</div>
