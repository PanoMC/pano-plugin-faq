<script context="module">
    import ApiUtil from '@panomc/sdk/utils/api';

    export async function load(event) {
        try {
            const res = await ApiUtil.get({ 
                path: '/api/faq/list',
                request: event
            });
            return res;
        } catch (e) {
            console.error('[FAQ] Hook load failed', e);
            return { faqs: [], categories: [], config: {} };
        }
    }
</script>

<script>
    import {onMount} from 'svelte';
    import {_} from '../../main';
    import FAQList from './FAQList.svelte';

    export let faqs = [];
    export let categories = [];
    export let config = {};

    let loading = faqs.length === 0;
    let searching = false;

    async function handleSearch(e) {
        const query = e.detail;
        searching = true;
        try {
            const res = await ApiUtil.get({ 
                path: '/api/faq/list' + (query ? `?search=${encodeURIComponent(query)}` : '') 
            });
            faqs = res.faqs;
        } catch (e) {
            console.error(e);
        } finally {
            searching = false;
        }
    }

    onMount(async () => {
        if (faqs.length === 0) {
            try {
                const res = await ApiUtil.get({ path: '/api/faq/list' });
                faqs = res.faqs;
                categories = res.categories;
                config = res.config;
            } catch (e) {
                console.error(e);
            } finally {
                loading = false;
            }
        } else {
            loading = false;
        }
    });
</script>

{#if !loading}
    <div class="mt-5" style:opacity={searching ? 0.6 : 1} style:transition="opacity 0.2s">
        <h2 class="mb-4 text-center">{$_('faq.title')}</h2>
        <FAQList {faqs} {categories} {config} on:search={handleSearch} />
    </div>
{/if}
