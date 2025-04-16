<script>
  import { onMount } from "svelte";
  import { createProject, updateProject } from "../services/api.js";

  let project = {
    name: "",
    description: "",
    status: "Pending",
  };

  let isEditing = false;
  let formError = null;
  let formSuccess = null;

  const statusOptions = ["Active", "Pending", "Completed"];

  function resetForm() {
    project = {
      name: "",
      description: "",
      status: "Pending",
    };
    isEditing = false;
  }

  async function handleSubmit() {
    try {
      formError = null;
      formSuccess = null;

      if (!project.name) {
        formError = "Project name is required";
        return;
      }

      if (isEditing) {
        await updateProject(project.id, project);
        formSuccess = "Project updated successfully!";
      } else {
        await createProject(project);
        formSuccess = "Project created successfully!";
        resetForm();
      }

      // Dispatch an event to refresh the project list
      document.dispatchEvent(new CustomEvent("project-saved"));

      // Clear success message after 3 seconds
      setTimeout(() => {
        formSuccess = null;
      }, 3000);
    } catch (err) {
      formError = err.message;
    }
  }

  function handleCancel() {
    resetForm();
  }

  // Listen for edit events from ProjectList component
  onMount(() => {
    const editHandler = (event) => {
      project = { ...event.detail };
      isEditing = true;
    };

    document.addEventListener("edit", editHandler);

    return () => {
      document.removeEventListener("edit", editHandler);
    };
  });
</script>

<div class="card">
  <div class="card-header">
    <h2 class="mb-0">{isEditing ? "Edit" : "Add"} Project</h2>
  </div>
  <div class="card-body">
    {#if formError}
      <div class="alert alert-danger mb-3">
        {formError}
      </div>
    {/if}

    {#if formSuccess}
      <div class="alert alert-success mb-3">
        {formSuccess}
      </div>
    {/if}

    <form on:submit|preventDefault={handleSubmit}>
      <div class="mb-3">
        <label for="name" class="form-label">Project Name</label>
        <input
          type="text"
          class="form-control"
          id="name"
          bind:value={project.name}
          required
        />
      </div>

      <div class="mb-3">
        <label for="description" class="form-label">Description</label>
        <textarea
          class="form-control"
          id="description"
          rows="3"
          bind:value={project.description}
        ></textarea>
      </div>

      <div class="mb-3">
        <label for="status" class="form-label">Status</label>
        <select class="form-select" id="status" bind:value={project.status}>
          {#each statusOptions as status}
            <option value={status}>{status}</option>
          {/each}
        </select>
      </div>

      <div class="d-flex gap-2">
        <button type="submit" class="btn btn-primary">
          {isEditing ? "Update" : "Create"} Project
        </button>

        {#if isEditing}
          <button
            type="button"
            class="btn btn-secondary"
            on:click={handleCancel}
          >
            Cancel
          </button>
        {/if}
      </div>
    </form>
  </div>
</div>
