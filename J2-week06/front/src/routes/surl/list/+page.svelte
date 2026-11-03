<script lang="ts">
	import type { components } from '$lib/backend/apiV1/schema';
	import rq from '$lib/rq/rq.svelte';

	let surls = $state<components['schemas']['SurlDto'][]>([]);

	async function getSurls() {
		const { data, error } = await rq.getClient().GET('/api/v1/surls');

		if (data) {
			surls = data.data.items;
		}
	}

	async function deleteSurl(surl: components['schemas']['SurlDto']) {
		const { data, error } = await rq.getClient().DELETE(`/api/v1/surls/{id}`, {
			params: {
				path: {
					id: surl.id
				}
			}
		});

		if (data) {
			surls.splice(surls.indexOf(surl), 1);
		} else if (error) {
			error.msg && alert(error.msg);
		}
	}

	$effect(() => {
		getSurls();
	});
</script>

<h1>SURL 목록</h1>

<ul>
	{#each surls as surl (surl.id)}
		<li>
			<a href="/surl/{surl.id}">{surl.id}</a> : {surl.url}
			<br />
			{surl.body}

			<a href="/surl/{surl.id}/edit">수정</a>

			<button
				type="button"
				on:click|preventDefault={() => confirm('정말로 삭제하시겠습니까?') && deleteSurl(surl)}
				>삭제</button
			>
		</li>
	{/each}
</ul>
