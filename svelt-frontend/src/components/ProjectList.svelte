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
    if (confirm("이 프로젝트를 삭제하시겠습니까?")) {
      try {
        await deleteProject(id);
        // 삭제 후 프로젝트 목록 다시 로드
        await loadProjects();
      } catch (err) {
        error = err.message;
      }
    }
  }

  function editProject(project) {
    // 부모 컴포넌트에서 캐치할 커스텀 이벤트 발송
    const event = new CustomEvent("edit", { detail: project });
    document.dispatchEvent(event);
  }

  onMount(() => {
    loadProjects();

    // Listen for project-saved event
    const savedHandler = () => {
      loadProjects();
    };

    document.addEventListener("project-saved", savedHandler);

    return () => {
      document.removeEventListener("project-saved", savedHandler);
    };
  });
</script>

<div class="card">
  <div class="card-header d-flex justify-content-between align-items-center">
    <h2 class="mb-0">프로젝트</h2>
    <button class="btn btn-outline-primary btn-sm" on:click={loadProjects}>
      새로고침
    </button>
  </div>
  <div class="card-body">
    {#if loading}
      <div class="d-flex justify-content-center">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">로딩중...</span>
        </div>
      </div>
    {:else if error}
      <div class="alert alert-danger">
        {error}
      </div>
    {:else if projects.length === 0}
      <p class="text-center">프로젝트가 없습니다. 새로 만들어보세요!</p>
    {:else}
      <div class="table-responsive">
        <table class="table table-hover">
          <thead>
            <tr>
              <th>ID</th>
              <th>이름</th>
              <th>상태</th>
              <th>작업</th>
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
                      수정
                    </button>
                    <button
                      class="btn btn-outline-danger"
                      on:click={() => handleDelete(project.id)}
                    >
                      삭제
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
