export async function handle({ event, resolve }) {
	// 경로가 숫자로만 구성되어 있는지 확인합니다.
	if (/^\/\d+$/.test(event.url.pathname)) {
		return new Response(null, {
			status: 301,
			headers: {
				Location: `${import.meta.env.VITE_CORE_API_BASE_URL}/g${event.url.pathname}`
			}
		});
	}

	const response = await resolve(event);
	return response;
}
