<script>
  import { onMount, createEventDispatcher } from "svelte";
  import { createProject, updateProject } from "../services/api.js";

  const dispatch = createEventDispatcher();

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
        formError = "프로젝트 이름은 필수입니다";
        return;
      }

      if (isEditing) {
        await updateProject(project.id, project);
        formSuccess = "프로젝트가 성공적으로 업데이트되었습니다!";
      } else {
        await createProject(project);
        formSuccess = "프로젝트가 성공적으로 생성되었습니다!";
        resetForm();
      }

      // 프로젝트 목록을 새로고침하기 위한 이벤트 발송
      document.dispatchEvent(new CustomEvent("project-saved"));

      // Dispatch event for the parent component
      dispatch("projectAdded", { success: true });

      // 3초 후 성공 메시지 제거
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

  // ProjectList 컴포넌트에서 편집 이벤트 수신
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
    <h2 class="mb-0">{isEditing ? "수정" : "추가"} 프로젝트</h2>
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
        <label for="name" class="form-label">프로젝트 이름</label>
        <input
          type="text"
          class="form-control"
          id="name"
          bind:value={project.name}
          required
        />
      </div>

      <div class="mb-3">
        <label for="description" class="form-label">설명</label>
        <textarea
          class="form-control"
          id="description"
          rows="3"
          bind:value={project.description}
        ></textarea>
      </div>

      <div class="mb-3">
        <label for="status" class="form-label">상태</label>
        <select class="form-select" id="status" bind:value={project.status}>
          {#each statusOptions as status}
            <option value={status}>{status}</option>
          {/each}
        </select>
      </div>

      <div class="d-flex gap-2">
        <button type="submit" class="btn btn-primary">
          프로젝트 {isEditing ? "업데이트" : "생성"}
        </button>

        {#if isEditing}
          <button
            type="button"
            class="btn btn-secondary"
            on:click={handleCancel}
          >
            취소
          </button>
        {/if}
      </div>
    </form>
  </div>
</div>
