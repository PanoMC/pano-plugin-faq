<script>
    import {onMount} from 'svelte';
    import {_} from '../main';
    import ApiUtil from '@panomc/sdk/utils/api';
    import {PageTitle} from '@panomc/sdk/components/theme';
    import FAQList from './components/FAQList.svelte';

    let faqs = [];
    let categories = [];
    let loading = true;
    let config = {};

    onMount(async () => {
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
    });
</script>

<div class="vstack gap-3">
    <PageTitle title={$_('faq.title')} />

    {#if loading}
         <div class="text-center py-5">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
        </div>
    {:else}
        <FAQList {faqs} {categories} _={$_} />
    {/if}
</div>
