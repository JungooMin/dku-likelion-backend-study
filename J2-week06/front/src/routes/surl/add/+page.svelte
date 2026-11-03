<script lang="ts">
	import rq from '$lib/rq/rq.svelte';

	async function submitAddForm(this: HTMLFormElement) {
		const form: HTMLFormElement = this;

		form.url.value = form.url.value.trim();

		if (form.url.value.length === 0) {
			alert('URL을 입력해주세요.');
			form.url.focus();
			return;
		}

		form.body.value = form.body.value.trim();

		const { data, error } = await rq.getClient().POST('/api/v1/surls', {
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
</script>

<h1>SURL 등록</h1>

<form on:submit|preventDefault={submitAddForm}>
	<div>
		<label>
			<span>URL</span>
			<input type="url" name="url" placeholder="URL" />
		</label>
	</div>
	<div>
		<label>
			<span>내용</span>
			<input type="text" name="body" placeholder="설명" />
		</label>
	</div>
	<div>
		<label>
			<span>등록</span>
			<input type="submit" value="등록" />
		</label>
	</div>
</form>
