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
          oninput={handleSearch} />
      </div>
    </div>
  {/if}

  {#if filteredFAQs.length === 0}
    <NoContent text={$_('faq.no_faqs')} />
  {:else}
    {#each groupedFAQs as category (category.id)}
      {#if category.items.length > 0}
        <div class="badge text-bg-primary mx-auto">{category.name}</div>
        <div class="accordion accordion-flush">
          {#each category.items as faq (faq.id)}
            <div class="accordion-item">
              <h2 class="accordion-header">
                <button
                  class="accordion-button fw-bolder fs-5"
                  type="button"
                  class:collapsed={activeFaqId !== faq.id}
                  onclick={() => toggleFaq(faq.id)}>
                  {faq.question}
                </button>
              </h2>
              {#if activeFaqId === faq.id}
                <div class="accordion-collapse collapse show" transition:slide>
                  <div class="accordion-body text-gray">
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
      <div class="accordion accordion-flush">
        {#each uncategorizedFAQs as faq (faq.id)}
          <div class="accordion-item">
            <h2 class="accordion-header">
              <button
                class="accordion-button fw-bolder fs-5"
                type="button"
                class:collapsed={activeFaqId !== faq.id}
                onclick={() => toggleFaq(faq.id)}>
                {faq.question}
              </button>
            </h2>
            {#if activeFaqId === faq.id}
              <div class="accordion-collapse collapse show" transition:slide>
                <div class="accordion-body text-gray">
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
  import {slide} from 'svelte/transition';
  import {_} from '../../main';
  import {NoContent} from '@panomc/sdk/components/theme';

  let { faqs = [], categories = [], config = { showSearch: true, questionLimit: 0 }, search = '', isSearching = false, onsearch } = $props();

  let searchQuery = $state('');
  let activeFaqId = $state(null);
  let searchTimeout;

  $effect(() => { searchQuery = search; });

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
      if (onsearch) onsearch(searchQuery);
    }, 500);
  }

  let limit = $derived(config.questionLimit && config.questionLimit > 0 ? config.questionLimit : Infinity);

  // Filter Logic - Server side handles search, but we might still have a limit
  let filteredFAQs = $derived(faqs.slice(0, limit));

  let groupedFAQs = $derived(categories.map((cat) => ({
    ...cat,
    items: filteredFAQs.filter((f) => f.categoryId === cat.id),
  })));

  let uncategorizedFAQs = $derived(filteredFAQs.filter((f) => !f.categoryId));
</script>
