<script lang="ts">
	import { page } from '$app/stores';
	import type { components } from '$lib/backend/apiV1/schema';
	import rq from '$lib/rq/rq.svelte';

	let surl = $state<components['schemas']['SurlDto'] | null>(null);
	let errorMessage = $state<string | null>(null);

	async function getSurl() {
		const { data, error } = await rq.getClient().GET('/api/v1/surls/{id}', {
			params: {
				path: {
					id: parseInt($page.params.id)
				}
			}
		});

		if (data) {
			surl = data.data.item;
		} else if (error) {
			errorMessage = error.msg;
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
		getSurl();
	});
</script>

<h1>{$page.params.id}번 SURL 페이지</h1>

{#if surl}
	<div>
		ID : {surl.id}
	</div>

	<div>
		생성날짜 : {surl.createDate}
	</div>

	<div>
		수정날짜 : {surl.modifyDate}
	</div>

	<div>
		URL : {surl.url}
	</div>

	<div>
		내용 : {surl.body}
	</div>

	<div>
		<button type="button" onclick={() => history.back()}>뒤로가기</button>

		<a href="/surl/{surl.id}/edit">수정</a>

		<button type="button" onclick={() => confirm('정말로 삭제하시겠습니까?') && deleteSurl(surl)}
			>삭제</button
		>
	</div>
{:else if errorMessage}
	<div>{errorMessage}</div>
{/if}
