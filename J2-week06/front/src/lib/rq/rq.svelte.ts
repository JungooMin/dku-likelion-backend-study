import { goto } from '$app/navigation';

import createClient from 'openapi-fetch';

import type { paths, components } from '$lib/backend/apiV1/schema';

type Client = ReturnType<typeof createClient<paths>>;

const client: Client = createClient<paths>({
	baseUrl: import.meta.env.VITE_CORE_API_BASE_URL,
	credentials: 'include'
});

class Rq {
	public member: components['schemas']['MemberDto'];

	constructor() {
		this.member = this.makeReactivityMember();
	}

	private makeReactivityMember() {
		let id = $state(0);
		let createDate = $state('');
		let modifyDate = $state('');
		let username = $state('');
		let nickname = $state('');

		return {
			get id() {
				return id;
			},
			set id(value: number) {
				id = value;
			},
			get createDate() {
				return createDate;
			},
			set createDate(value: string) {
				createDate = value;
			},
			get modifyDate() {
				return modifyDate;
			},
			set modifyDate(value: string) {
				modifyDate = value;
			},
			get username() {
				return username;
			},
			set username(value: string) {
				username = value;
			},
			get nickname() {
				return nickname;
			},
			set nickname(value: string) {
				nickname = value;
			}
		};
	}

	public setLogined(member: components['schemas']['MemberDto']) {
		Object.assign(this.member, member);
	}

	public setLogout() {
		this.member.id = 0;
		this.member.createDate = '';
		this.member.modifyDate = '';
		this.member.username = '';
		this.member.nickname = '';
	}

	public isLogin() {
		return this.member.id !== 0;
	}

	public isLogout() {
		return !this.isLogin();
	}

	public async initAuth() {
		const { data } = await this.getClient().GET('/api/v1/members/me');

		if (data) {
			this.setLogined(data.data.item);
		}
	}

	public goto(url: string) {
		goto(url);
	}

	public replace(url: string) {
		goto(url, { replaceState: true });
	}

	public getClient() {
		return client;
	}
}

const rq = new Rq();

export default rq;
