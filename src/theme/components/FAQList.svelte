<div class="faq-list vstack gap-3">
  {#if config.showSearch}
    <div class="d-flex justify-content-center">
      <div class="position-relative">
        <div
          class="position-absolute top-50 start-0 translate-middle-y ms-3 z-3 text-muted"
          style="pointer-events: none;">
          {#if isSearching}
            <div class="spinner-border spinner-border-sm text-primary" role="status"></div>
          {:else}
            <i class="fa-solid fa-magnifying-glass"></i>
          {/if}
        </div>
        <input
          type="text"
          class="form-control rounded-pill ps-5"
          style="width: 300px;"
          placeholder={$_('faq.search')}
          bind:value={searchQuery}
          on:input={handleSearch} />
      </div>
    </div>
  {/if}

  {#if filteredFAQs.length === 0}
    <NoContent text={$_('faq.no_faqs')} />
  {:else}
    {#each groupedFAQs as category (category.id)}
      {#if category.items.length > 0}
        <div class="badge text-bg-primary">{category.name}</div>
        <div class="accordion">
          {#each category.items as faq (faq.id)}
            <div class="accordion-item">
              <h2 class="accordion-header fw-bolder">
                <button
                  class="accordion-button"
                  type="button"
                  class:collapsed={activeFaqId !== faq.id}
                  on:click={() => toggleFaq(faq.id)}>
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
      {/if}
    {/each}

    {#if uncategorizedFAQs.length > 0}
      {#if categories.length > 0}
        <div class="badge text-bg-primary mx-auto">{$_('faq.uncategorized')}</div>
      {/if}
      <div class="accordion">
        {#each uncategorizedFAQs as faq (faq.id)}
          <div class="accordion-item">
            <h2 class="accordion-header">
              <button
                class="accordion-button fw-bolder"
                type="button"
                class:collapsed={activeFaqId !== faq.id}
                on:click={() => toggleFaq(faq.id)}>
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
    {/if}
  {/if}
</div>

<script>
  import { slide } from 'svelte/transition';
  import { createEventDispatcher } from 'svelte';
  import { _ } from '../../main';
  import { NoContent } from '@panomc/sdk/components/theme';

  const dispatch = createEventDispatcher();

  export let faqs = [];
  export let categories = [];
  export let config = { showSearch: true, questionLimit: 0 };
  export let search = '';
  export let isSearching = false;

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

  $: groupedFAQs = categories.map((cat) => ({
    ...cat,
    items: filteredFAQs.filter((f) => f.categoryId === cat.id),
  }));

  $: uncategorizedFAQs = filteredFAQs.filter((f) => !f.categoryId);
</script>
