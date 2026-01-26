<script context="module">
    import ApiUtil from '@panomc/sdk/utils/api';

    export async function load(event) {
        const { url: { searchParams } } = event;
        const search = searchParams.get('search') || '';

        try {
            const res = await ApiUtil.get({ 
                path: '/api/faq/list' + (search ? `?search=${encodeURIComponent(search)}` : ''),
                request: event
            });
            
            return {
                data: {
                    faqs: res.faqs,
                    categories: res.categories,
                    config: res.config,
                    search
                }
            };
        } catch (e) {
            console.error('[FAQ] Failed to load data', e);
            return {
                data: {
                    faqs: [],
                    categories: [],
                    config: {},
                    search: ''
                }
            };
        }
    }
</script>

<script>
    import { _ } from '../main';
    import { PageTitle } from '@panomc/sdk/components/theme';
    import { goto, page } from '@panomc/sdk/svelte';
    import FAQList from './components/FAQList.svelte';

    export let data;
    $: ({ faqs, categories, config, search } = data);

    function handleSearch(e) {
        const query = e.detail;
        const url = new URL($page.url);
        if (query) {
            url.searchParams.set('search', query);
        } else {
            url.searchParams.delete('search');
        }
        goto(url.toString(), { keepfocus: true, noscroll: true });
    }
</script>

<div class="vstack gap-3">
    <PageTitle title={$_('faq.title')} />
    <FAQList {faqs} {categories} {config} {search} on:search={handleSearch} />
</div>
