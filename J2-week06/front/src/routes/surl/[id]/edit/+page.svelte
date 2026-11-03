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

	async function submitModifyForm(this: HTMLFormElement, event: Event) {
		event.preventDefault();
		const form: HTMLFormElement = this;

		form.url.value = form.url.value.trim();

		if (form.url.value.length === 0) {
			alert('URL을 입력해주세요.');
			form.url.focus();
			return;
		}

		form.body.value = form.body.value.trim();

		const { data, error } = await rq.getClient().PUT('/api/v1/surls/{id}', {
			params: {
				path: {
					id: parseInt($page.params.id)
				}
			},
			body: {
				url: form.url.value,
				body: form.body.value
			}
		});

		if (data) {
			data.msg && alert(data.msg);
			rq.replace('/surl/list');
		} else if (error) {
			error.msg && alert(error.msg);
		}
	}

	$effect(() => {
		getSurl();
	});
</script>

<h1>{$page.params.id}번 SURL 수정</h1>

{#if surl}
	<form onsubmit={submitModifyForm}>
		<div>
			<label>
				<span>URL</span>
				<input type="url" name="url" placeholder="URL" value={surl.url} />
			</label>
		</div>
		<div>
			<label>
				<span>내용</span>
				<input type="text" name="body" placeholder="설명" value={surl.body} />
			</label>
		</div>
		<div>
			<label>
				<span>수정</span>
				<input type="submit" value="수정" />
			</label>
			<button type="button" onclick={() => history.back()}>뒤로가기</button>
		</div>
		<div></div>
	</form>
{:else if errorMessage}
	<div>{errorMessage}</div>
{/if}
