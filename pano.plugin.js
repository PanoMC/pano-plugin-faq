// Plugin-level options of the Pano plugin kit (@panomc/plugin-kit). The namespace is `faq` (the plugin id minus
// `pano-plugin-`); the views are the files of src/theme/views.
export default {
  styles: {
    // `collapsed` is the Bootstrap accordion state class the question buttons toggle.
    safelist: ['collapsed'],
    // Views whose inline style= keeps today's markup: the search icon box and the 300px search field of FAQList, and
    // the fade of the support-page block while a search runs.
    styleAttrAllow: ['FAQList', 'SupportFAQWrapper'],
  },
};
