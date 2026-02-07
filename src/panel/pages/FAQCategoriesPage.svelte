<div class="container vstack gap-3">
  <!-- Action Menu -->
  <PageActions>
    <PageNav slot="left">
      <PageNavItem href="/faq">
        {$_('faq.list')}
      </PageNavItem>
      <PageNavItem href="/faq/categories">
        {$_('faq.categories')}
      </PageNavItem>
    </PageNav>
    <div slot="right">
      <button type="button" class="btn btn-secondary" on:click={openAddCategory}>
        <i class="fas fa-plus"></i>
        <span class="d-lg-inline d-none ms-2">{$_('faq.add_category')}</span>
      </button>
    </div>
  </PageActions>

  <div class="card">
    <CardHeader>
      <div slot="left">
        {categoryCount} {$_('faq.category_count_label')}
      </div>
      <div slot="middle" style="width: 250px;">
        <div class="input-group">
          <input
            type="text"
            class="form-control form-control-sm {String(searchQuery || '').trim()
              ? 'border-secondary'
              : ''}"
            bind:value={searchQuery}
            on:input={(e) => onSearchInput(e.target.value)}
            placeholder={$_('faq.search_category')} />
          {#if searching}
            <span class="input-group-text">
              <span class="spinner-border spinner-border-sm text-secondary" role="status"></span>
            </span>
          {/if}
        </div>
      </div>
    </CardHeader>

    {#if categories.length === 0}
      <NoContent />
    {:else}
      <div class="table-responsive">
        <table class="table table-hover">
          <thead>
            <tr>
              <th scope="col" class="align-middle text-center" style="width: 60px;"></th>
              <!-- Action Column -->
              <th scope="col" class="align-middle text-center" style="width: 60px;">ID</th>
              <th scope="col" class="align-middle">{$_('faq.category_modal.name')}</th>
              <th scope="col" class="align-middle text-center" style="width: 150px;"
                >{$_('faq.display_order')}</th>
            </tr>
          </thead>
          <tbody>
            {#each categories as category (category.id)}
              <FAQCategoryRow {category} onEdit={openEditCategory} onDelete={openDeleteCategory} />
            {/each}
          </tbody>
        </table>
      </div>
      <div class="card-footer">
        <Pagination page={data.page} {totalPage} on:change={onPageChange} />
      </div>
    {/if}
  </div>

  <!-- Modals -->
  <AddEditFAQCategoryModal />
  <ConfirmDeleteFAQCategoryModal />
</div>

<script context="module">
  import ApiUtil, {buildQueryParams} from '@panomc/sdk/utils/api';

  export async function load(event) {
    const {
      parent,
      url: { searchParams },
    } = event;
    const { pageTitle } = await parent();
    pageTitle.set('plugins.pano-plugin-faq.faq.categories');

    const page = searchParams.get('page') || 1;
    const search = searchParams.get('search');

    const queryParams = buildQueryParams({
      page,
      search,
    });

    try {
      const res = await ApiUtil.get({
        path: '/api/panel/faq/category/list' + queryParams,
        request: event,
      });

      return {
        data: res,
      };
    } catch (e) {
      console.error('[FAQ] Failed to load data', e);
      return {
        data: { categories: [], categoryCount: 0, totalPage: 1, page: 1 },
      };
    }
  }
</script>

<script>
  import { base, page, goto } from '@panomc/sdk/svelte';
  import {
    PageActions,
    CardHeader,
    NoContent,
    Pagination,
    PageNav,
    PageNavItem,
  } from '@panomc/sdk/components/panel';
  import { _ } from '../../main';
  import { buildQueryParams as createQueryParams } from '@panomc/sdk/utils/api';

  import FAQCategoryRow from '../components/FAQCategoryRow.svelte';
  import AddEditFAQCategoryModal, {
    show as showAddEditCategoryModal,
    setCallback as setAddEditCategoryCallback,
  } from '../components/modals/AddEditFAQCategoryModal.svelte';
  import ConfirmDeleteFAQCategoryModal, {
    show as showDeleteCategoryModal,
    setCallback as setDeleteCategoryCallback,
  } from '../components/modals/ConfirmDeleteFAQCategoryModal.svelte';

  export let data;

  $: ({ categories, categoryCount, totalPage } = data);
  $: searchQuery = $page.url.searchParams.get('search') || '';

  let searchTimeout;
  let searching = false;

  async function refreshData() {
    searching = true;
    const pageNum = data.page === 1 ? null : data.page;
    const searchVal = searchQuery || null;

    const queryParams = createQueryParams({
      page: pageNum,
      search: searchVal,
    });

    try {
      await goto(base + `/faq/categories${queryParams}`, {
        invalidateAll: true,
        keepFocus: true,
        noscroll: true,
      });
    } finally {
      searching = false;
    }
  }

  function onSearchInput(value) {
    searchQuery = value;
    data.page = 1;
    clearTimeout(searchTimeout);
    searchTimeout = setTimeout(() => {
      refreshData();
    }, 500);
  }

  setAddEditCategoryCallback(refreshData);
  setDeleteCategoryCallback(refreshData);

  function openAddCategory() {
    showAddEditCategoryModal('create', null);
  }

  function openEditCategory(category) {
    showAddEditCategoryModal('edit', category);
  }

  function openDeleteCategory(category) {
    showDeleteCategoryModal(category);
  }

  function onPageChange(e) {
    data.page = e.detail;
    refreshData();
  }
</script>
