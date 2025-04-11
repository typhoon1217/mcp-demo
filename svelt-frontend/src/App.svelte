<script>
  import ProjectList from "./components/ProjectList.svelte";
  import ProjectForm from "./components/ProjectForm.svelte";
  import MCPDemo from "./components/MCPDemo.svelte";

  let activeTab = "projects";
  let showProjectForm = false;

  function toggleProjectForm() {
    showProjectForm = !showProjectForm;
  }
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
  </ul>

  <div class="tab-content">
    {#if activeTab === "projects"}
      <div class="mb-3">
        <button class="btn btn-primary" on:click={toggleProjectForm}>
          Add New Project
        </button>
      </div>
      <div class="resizable-container">
        <div class="resizable-widget">
          <ProjectList />
        </div>
      </div>
    {:else if activeTab === "mcp"}
      <div class="resizable-container">
        <div class="resizable-widget">
          <MCPDemo />
        </div>
      </div>
    {/if}
  </div>

  <!-- Project Form Modal -->
  {#if showProjectForm}
    <div class="modal-backdrop" on:click={toggleProjectForm}>
      <div class="modal-content" on:click|stopPropagation>
        <div class="modal-header">
          <h5 class="modal-title">Add New Project</h5>
          <button type="button" class="btn-close" on:click={toggleProjectForm}
          ></button>
        </div>
        <div class="modal-body">
          <ProjectForm on:projectAdded={toggleProjectForm} />
        </div>
      </div>
    </div>
  {/if}
</main>

<style>
  :global(body) {
    font-family: Arial, sans-serif;
    padding-bottom: 2rem;
  }

  .resizable-container {
    width: 100%;
    margin-bottom: 1rem;
  }

  .resizable-widget {
    border: 1px solid #ddd;
    border-radius: 4px;
    padding: 1rem;
    overflow: auto;
    resize: both;
    min-height: 300px;
    min-width: 300px;
    max-width: 100%;
    background-color: white;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  }

  /* Modal styles */
  .modal-backdrop {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1050;
  }

  .modal-content {
    background-color: white;
    border-radius: 5px;
    width: 90%;
    max-width: 600px;
    max-height: 90vh;
    overflow-y: auto;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  }

  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem;
    border-bottom: 1px solid #dee2e6;
  }

  .modal-body {
    padding: 1rem;
  }
</style>
