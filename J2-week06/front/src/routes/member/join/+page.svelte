<script lang="ts">
	import rq from '$lib/rq/rq.svelte';

	async function submitJoinForm(this: HTMLFormElement) {
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

		form.passwordConfirm.value = form.passwordConfirm.value.trim();

		if (form.passwordConfirm.value.length === 0) {
			alert('비밀번호 확인을 입력해주세요.');
			form.passwordConfirm.focus();
			return;
		}

		if (form.password.value != form.passwordConfirm.value) {
			alert('비밀번호가 일치하지 않습니다.');
			form.passwordConfirm.focus();
			return;
		}

		form.nickname.value = form.nickname.value.trim();

		if (form.nickname.value.length === 0) {
			alert('별명을 입력해주세요.');
			form.nickname.focus();
			return;
		}

		const { data, error } = await rq.getClient().POST('/api/v1/members', {
			body: {
				username: form.username.value,
				password: form.password.value,
				nickname: form.nickname.value
			}
		});

		if (data) {
			data.msg && alert(data.msg);
			rq.goto('/member/login');
		} else if (error) {
			error.msg && alert(error.msg);
		}
	}
</script>

<h1>회원가입</h1>

<form on:submit|preventDefault={submitJoinForm}>
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
			<span>비밀번호 확인</span>
			<input type="password" name="passwordConfirm" placeholder="비밀번호 확인" />
		</label>
	</div>
	<div>
		<label>
			<span>별명</span>
			<input type="text" name="nickname" placeholder="별명" />
		</label>
	</div>
	<div>
		<label>
			<span>회원가입</span>
			<input type="submit" value="회원가입" />
		</label>
	</div>
</form>
