<script lang="ts">
	import rq from '$lib/rq/rq.svelte';
</script>

<h1>내 정보</h1>

{#if rq.isLogin()}
	<div>
		<div>
			<span>아이디</span>
			<span>{rq.member.username}</span>
		</div>
		<div>
			<span>닉네임</span>
			<span>{rq.member.nickname}</span>
		</div>
		<div>
			<span>가입일</span>
			<span>{rq.member.createDate}</span>
		</div>
		<div>
			<span>수정일</span>
			<span>{rq.member.modifyDate}</span>
		</div>
	</div>
{:else}
	<div>
		<a href="/member/login">로그인</a> 후 이용해주세요.
	</div>
{/if}
