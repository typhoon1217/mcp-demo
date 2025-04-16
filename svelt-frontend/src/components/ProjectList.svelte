<script>
  import { onMount } from "svelte";
  import { getAllProjects, deleteProject } from "../services/api.js";

  let projects = [];
  let loading = true;
  let error = null;

  async function loadProjects() {
    try {
      loading = true;
      projects = await getAllProjects();
    } catch (err) {
      error = err.message;
    } finally {
      loading = false;
    }
  }

  async function handleDelete(id) {
    if (confirm("Are you sure you want to delete this project?")) {
      try {
        await deleteProject(id);
        // Reload projects after deletion
        await loadProjects();
      } catch (err) {
        error = err.message;
      }
    }
  }

  function editProject(project) {
    // Dispatch custom event that will be caught by the parent
    const event = new CustomEvent("edit", { detail: project });
    document.dispatchEvent(event);
  }

  onMount(loadProjects);
</script>

<div class="card">
  <div class="card-header d-flex justify-content-between align-items-center">
    <h2 class="mb-0">Projects</h2>
    <button class="btn btn-outline-primary btn-sm" on:click={loadProjects}>
      Refresh
    </button>
  </div>
  <div class="card-body">
    {#if loading}
      <div class="d-flex justify-content-center">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>
    {:else if error}
      <div class="alert alert-danger">
        {error}
      </div>
    {:else if projects.length === 0}
      <p class="text-center">No projects found. Create one!</p>
    {:else}
      <div class="table-responsive">
        <table class="table table-hover">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {#each projects as project}
              <tr>
                <td>{project.id}</td>
                <td>{project.name}</td>
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
                <td>
                  <div class="btn-group btn-group-sm">
                    <button
                      class="btn btn-outline-primary"
                      on:click={() => editProject(project)}
                    >
                      Edit
                    </button>
                    <button
                      class="btn btn-outline-danger"
                      on:click={() => handleDelete(project.id)}
                    >
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            {/each}
          </tbody>
        </table>
      </div>
    {/if}
  </div>
</div>
