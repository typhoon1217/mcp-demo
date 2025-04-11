const API_URL = 'http://localhost:8080/api';

export async function sendQueryToMCP(query) {
    const response = await fetch(`${API_URL}/mcp/query`, {
        method: 'POST',
        headers: {
            'Content-Type': 'text/plain'
        },
        body: query
    });
    
    if (!response.ok) {
        throw new Error('Failed to process MCP query');
    }
    
    return await response.json();
}
