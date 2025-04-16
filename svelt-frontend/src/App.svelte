<script>
  import ProjectList from "./components/ProjectList.svelte";
  import ProjectForm from "./components/ProjectForm.svelte";
  import MCPDemo from "./components/MCPDemo.svelte";
  import MCPAction from "./components/MCPAction.svelte"; // New component

  let activeTab = "projects";
</script>

<main class="container mt-4">
  <h1 class="mb-4">MCP Demo with Spring Boot + Svelte</h1>

  <ul class="nav nav-tabs mb-4">
    <li class="nav-item">
      <button
        class="nav-link {activeTab === 'projects' ? 'active' : ''}"
        on:click={() => (activeTab = "projects")}
      >
        Projects CRUD
      </button>
    </li>
    <li class="nav-item">
      <button
        class="nav-link {activeTab === 'mcp' ? 'active' : ''}"
        on:click={() => (activeTab = "mcp")}
      >
        MCP Demo
      </button>
    </li>
    <li class="nav-item">
      <button
        class="nav-link {activeTab === 'mcpAction' ? 'active' : ''}"
        on:click={() => (activeTab = "mcpAction")}
      >
        AI Project Management
      </button>
    </li>
  </ul>

  <div class="tab-content">
    {#if activeTab === "projects"}
      <div class="row">
        <div class="col-md-5">
          <ProjectForm />
        </div>
        <div class="col-md-7">
          <ProjectList />
        </div>
      </div>
    {:else if activeTab === "mcp"}
      <MCPDemo />
    {:else if activeTab === "mcpAction"}
      <MCPAction />
    {/if}
  </div>
</main>

<style>
  :global(body) {
    font-family: Arial, sans-serif;
    padding-bottom: 2rem;
  }
</style>
