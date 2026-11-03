<script lang="ts">
	import rq from '$lib/rq/rq.svelte';

	async function submitLoginForm(this: HTMLFormElement) {
		const form: HTMLFormElement = this;

		form.username.value = form.username.value.trim();

		if (form.username.value.length === 0) {
			alert('아이디를 입력해주세요.');
			form.username.focus();
			return;
		}

		form.password.value = form.password.value.trim();

		if (form.password.value.length === 0) {
			alert('비밀번호를 입력해주세요.');
			form.password.focus();
			return;
		}

		const { data, error } = await rq.getClient().POST('/api/v1/members/login', {
			body: {
				username: form.username.value,
				password: form.password.value
			}
		});

		if (data) {
			data.msg && alert(data.msg);
			rq.setLogined(data.data.item);
			rq.goto('/');
		} else if (error) {
			error.msg && alert(error.msg);
		}
	}
</script>

<h1>로그인</h1>

<form on:submit|preventDefault={submitLoginForm}>
	<div>
		<label>
			<span>아이디</span>
			<input type="text" name="username" placeholder="아이디" />
		</label>
	</div>
	<div>
		<label>
			<span>비밀번호</span>
			<input type="password" name="password" placeholder="비밀번호" />
		</label>
	</div>
	<div>
		<label>
			<span>로그인</span>
			<input type="submit" value="로그인" />
		</label>
	</div>
</form>
