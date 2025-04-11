const API_URL = 'http://localhost:8080/api';

export async function getAllProjects() {
    const response = await fetch(`${API_URL}/projects`);
    if (!response.ok) {
        throw new Error('Failed to fetch projects');
    }
    return await response.json();
}

export async function getProjectById(id) {
    const response = await fetch(`${API_URL}/projects/${id}`);
    if (!response.ok) {
        throw new Error('Failed to fetch project');
    }
    return await response.json();
}

export async function createProject(project) {
    const response = await fetch(`${API_URL}/projects`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(project)
    });
    if (!response.ok) {
        throw new Error('Failed to create project');
    }
    return await response.json();
}

export async function updateProject(id, project) {
    const response = await fetch(`${API_URL}/projects/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(project)
    });
    if (!response.ok) {
        throw new Error('Failed to update project');
    }
    return await response.json();
}

export async function deleteProject(id) {
    const response = await fetch(`${API_URL}/projects/${id}`, {
        method: 'DELETE'
    });
    if (!response.ok) {
        throw new Error('Failed to delete project');
    }
    return true;
}
