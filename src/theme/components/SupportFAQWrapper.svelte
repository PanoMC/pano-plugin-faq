<script>
    import {onMount} from 'svelte';
    import {_} from '../../main';
    import ApiUtil from '@panomc/sdk/utils/api';
    import FAQList from './FAQList.svelte';

    let faqs = [];
    let categories = [];
    let loading = true;

    onMount(async () => {
        try {
            const res = await ApiUtil.get({ path: '/api/faq/list' });
            faqs = res.faqs;
            categories = res.categories;
        } catch (e) {
            console.error(e);
        } finally {
            loading = false;
        }
    });
</script>

{#if !loading && faqs.length > 0}
    <div class="mt-5">
        <h2 class="mb-4 text-center">{$_('faq.title')}</h2>
        <FAQList {faqs} {categories} _={$_} />
    </div>
{/if}
