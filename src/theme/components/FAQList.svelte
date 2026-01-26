<script>
    import {slide} from 'svelte/transition';
    import {createEventDispatcher} from 'svelte';
    import {_} from '../../main';

    const dispatch = createEventDispatcher();

    export let faqs = [];
    export let categories = [];
    export let config = { showSearch: true, questionLimit: 0 };
    export let search = '';

    let searchQuery = search;
    let activeFaqId = null;
    let searchTimeout;

    function toggleFaq(id) {
        if (activeFaqId === id) {
            activeFaqId = null;
        } else {
            activeFaqId = id;
        }
    }

    function handleSearch() {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(() => {
            dispatch('search', searchQuery);
        }, 500);
    }

    $: limit = config.questionLimit && config.questionLimit > 0 ? config.questionLimit : Infinity;

    // Filter Logic - Server side handles search, but we might still have a limit
    $: filteredFAQs = faqs.slice(0, limit);

    $: groupedFAQs = categories.map(cat => ({
        ...cat,
        items: filteredFAQs.filter(f => f.categoryId === cat.id)
    }));

    $: uncategorizedFAQs = filteredFAQs.filter(f => !f.categoryId);
</script>

<div class="faq-list">
    {#if config.showSearch}
        <div class="mb-4">
            <input type="text" class="form-control form-control-lg" placeholder={$_('faq.search')} bind:value={searchQuery} on:input={handleSearch}>
        </div>
    {/if}

    {#if filteredFAQs.length === 0}
         <div class="text-center py-5 text-muted">
            <i class="fas fa-question-circle fa-3x mb-3"></i>
            <p>{$_('faq.no_faqs')}</p>
        </div>
    {:else}
        {#each groupedFAQs as category (category.id)}
            {#if category.items.length > 0}
            <div class="mb-4">
                <h3 class="mb-3 border-bottom pb-2">{category.name}</h3>
                <div class="accordion">
                    {#each category.items as faq (faq.id)}
                        <div class="accordion-item">
                            <h2 class="accordion-header">
                                <button class="accordion-button" type="button" class:collapsed={activeFaqId !== faq.id} on:click={() => toggleFaq(faq.id)}>
                                    {faq.question}
                                </button>
                            </h2>
                            {#if activeFaqId === faq.id}
                            <div class="accordion-collapse collapse show" transition:slide|local>
                                <div class="accordion-body">
                                    {@html faq.answer}
                                </div>
                            </div>
                            {/if}
                        </div>
                    {/each}
                </div>
            </div>
            {/if}
        {/each}

        {#if uncategorizedFAQs.length > 0}
             <div class="mb-4">
                {#if categories.length > 0}
                    <h3 class="mb-3 border-bottom pb-2">{$_('faq.uncategorized')}</h3>
                {/if}
                <div class="accordion">
                    {#each uncategorizedFAQs as faq (faq.id)}
                         <div class="accordion-item">
                            <h2 class="accordion-header">
                                <button class="accordion-button" type="button" class:collapsed={activeFaqId !== faq.id} on:click={() => toggleFaq(faq.id)}>
                                    {faq.question}
                                </button>
                            </h2>
                            {#if activeFaqId === faq.id}
                            <div class="accordion-collapse collapse show" transition:slide|local>
                                <div class="accordion-body">
                                    {@html faq.answer}
                                </div>
                            </div>
                            {/if}
                        </div>
                    {/each}
                </div>
            </div>
        {/if}
    {/if}
</div>
